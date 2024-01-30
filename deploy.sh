#!/bin/bash
# © Copyright IBM Corporation 2023, 2024
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

function line_separator () {
  echo "####################### $1 #######################"
}

NAMESPACE=${1:-"cp4i"}
BLOCK_STORAGE=${2:-"ocs-storagecluster-ceph-rbd"}
INSTALL_CP4I=${5:-true}
MQ_TEMPLATE=${3:-"orders.yaml_template"}
ENABLE_CONNECTOR=${4:-false}

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

if [ -z $NAMESPACE ]
then
    echo "Usage: deploy.sh <namespace for deployment> <file storage class> <block storage class>"
    exit 1
fi

oc new-project $NAMESPACE 2> /dev/null
oc project $NAMESPACE

if [ "$INSTALL_CP4I" = true ] ; then
  oc patch storageclass $BLOCK_STORAGE -p '{"metadata": {"annotations":{"storageclass.kubernetes.io/is-default-class":"true"}}}'
fi

./install-operators.sh

if [ "$INSTALL_CP4I" = true ] ; then
  echo ""
  line_separator "START - INSTALLING PLATFORM NAVIGATOR"
  cat $SCRIPT_DIR/platformnav/platform-nav.yaml_template |
  sed "s#{{NAMESPACE}}#$NAMESPACE#g;" > $SCRIPT_DIR/platformnav/platform-nav.yaml

  oc apply -f platformnav/platform-nav.yaml
  sleep 30

  END=$((SECONDS+3600))
  PLATFORM_NAV=FAILED

  while [ $SECONDS -lt $END ]; do
    PLATFORM_NAV_PHASE=$(oc get platformnavigator platform-navigator -o=jsonpath={'.status.conditions[].type'})
    if [[ $PLATFORM_NAV_PHASE == "Ready" ]]
    then
      echo "Platform Navigator available"
      PLATFORM_NAV=SUCCESS
      break
    else
      echo "Waiting for Platform Navigator to be available"
      sleep 60
    fi
  done

  if [[ $PLATFORM_NAV == "SUCCESS" ]]
  then
    echo "SUCCESS"
  else
    echo "ERROR: Platform Navigator failed to install after 60 minutes"
    exit 1
  fi
  line_separator "SUCCESS - INSTALLING PLATFORM NAVIGATOR"
fi

echo ""
line_separator "START - INSTALLING IBM MQ"

cat $SCRIPT_DIR/mq/$MQ_TEMPLATE |
  sed "s#{{NAMESPACE}}#$NAMESPACE#g;" |
  sed "s#{{FILE_STORAGE}}#$FILE_STORAGE#g;" |
  sed "s#{{BLOCK_STORAGE}}#$BLOCK_STORAGE#g;" > $SCRIPT_DIR/mq/orders.yaml

oc apply -f mq/orders.yaml


END=$((SECONDS+3600))
MQ=FAILED
while [ $SECONDS -lt $END ]; do
    MQ_PHASE=$(oc get qmgr orders -o=jsonpath={'..phase'})
    if [[ $MQ_PHASE == "Running" ]]
    then
      echo "MQ available"
      MQ=SUCCESS
      break
    else
      echo "Waiting for MQ to be available - this may take 5 minutes"
      sleep 60
    fi
done

line_separator "SUCCESS - IBM MQ CREATED"

echo ""
line_separator "START - INSTALLING IBM EVENT STREAMS"
cat $SCRIPT_DIR/event-streams/createEventStream.yaml_template |
  sed "s#{{NAMESPACE}}#$NAMESPACE#g;" |
  sed "s#{{FILE_STORAGE}}#$FILE_STORAGE#g;" |
  sed "s#{{BLOCK_STORAGE}}#$BLOCK_STORAGE#g;" > $SCRIPT_DIR/event-streams/createEventStream.yaml

oc apply -f event-streams/createEventStream.yaml


END=$((SECONDS+3600))
EVENT_STREAM=FAILED
while [ $SECONDS -lt $END ]; do
    ES_PHASE=$(oc get EventStreams ademo-es -o=jsonpath={'..phase'})
    if [[ $ES_PHASE == "Ready" ]]
    then
      echo "Event Streams available"
      EVENT_STREAM=SUCCESS
      break
    else
      echo "Waiting for Event Stream to be available - this may take 5-10 minutes"
      sleep 60
    fi
done

line_separator "SUCCESS - IBM EVENT STREAMS CREATED"

echo ""
line_separator "START - INSTALLING IBM EVENT ENDPOINT MANAGEMENT"

cat $SCRIPT_DIR/event-endpoint-management/createEventEndpointManager.yaml_template |
  sed "s#{{NAMESPACE}}#$NAMESPACE#g;" | 
  sed "s#{{BLOCK_STORAGE}}#$BLOCK_STORAGE#g;" > $SCRIPT_DIR/event-endpoint-management/createEventEndpointManager.yaml

oc apply -f event-endpoint-management/createEventEndpointManager.yaml


END=$((SECONDS+3600))
EVENT_MANAGEMENT=FAILED
while [ $SECONDS -lt $END ]; do
    ES_PHASE=$(oc get EventEndpointManagement ademo-eem -o=jsonpath={'..phase'})
    if [[ $ES_PHASE == "Running" ]]
    then
      echo "Event Endpoint Management available"
      EVENT_MANAGEMENT=SUCCESS
      break
    else
      echo "Waiting for Event Endpoint Management to be available - this may take 5-10 minutes"
      sleep 60
    fi
done

rm event-endpoint-management/createEventEndpointManager.yaml


if [[ $EVENT_MANAGEMENT == "SUCCESS" ]]
then
  echo "SUCCESS"
else
  echo "ERROR: IBM Event Endpoint Management failed to install after 60 minutes"
  exit 1
fi


EEM_GATEWAY_URL=$(oc get eem ademo-eem -o=jsonpath='{.status.endpoints[?(@.name=="gateway")].uri}')
cat $SCRIPT_DIR/event-endpoint-management/createEventGateway.yaml_template |
  sed "s#{{NAMESPACE}}#$NAMESPACE#g;" | 
  sed "s#{{EEM_GATEWAY_URL}}#$EEM_GATEWAY_URL#g;" > $SCRIPT_DIR/event-endpoint-management/createEventGateway.yaml

oc apply -f event-endpoint-management/createEventGateway.yaml


END=$((SECONDS+3600))
EVENT_GATEWAY=FAILED
while [ $SECONDS -lt $END ]; do
    EG_PHASE=$(oc get EventGateway ademo-event-gw -o=jsonpath={'..phase'})
    if [[ $EG_PHASE == "Running" ]]
    then
      echo "Event Gateway available"
      EVENT_GATEWAY=SUCCESS
      break
    else
      echo "Waiting for Event Gateway to be available - this may take 5-10 minutes"
      sleep 60
    fi
done

rm event-endpoint-management/createEventGateway.yaml


if [[ $EVENT_GATEWAY == "SUCCESS" ]]
then
  echo "SUCCESS"
else
  echo "ERROR: IBM Event Gateway failed to install after 60 minutes"
  exit 1
fi

line_separator "SUCCESS - IBM EVENT ENDPOINT MANAGEMENT CREATED"

echo ""
line_separator "START - INSTALLING FLINK"

oc apply -f event-processing/flink.yaml


END=$((SECONDS+3600))
FLINK=FAILED
while [ $SECONDS -lt $END ]; do
    FLINK_PHASE=$(oc get FlinkDeployment my-flink -o=jsonpath={'..state'})
    if [[ $FLINK_PHASE == "DEPLOYED" ]]
    then
      echo "Flink available"
      FLINK=SUCCESS
      break
    else
      echo "Waiting for Flink to be available - this may take 5-10 minutes"
      sleep 60
    fi
done


if [[ $FLINK == "SUCCESS" ]]
then
  echo "SUCCESS"
else
  echo "ERROR: IBM Event Endpoint Management failed to install after 60 minutes"
  exit 1
fi

line_separator "SUCCESS - FLINK CREATED"

echo ""
line_separator "START - INSTALLING IBM EVENT PROCESSING"

cat $SCRIPT_DIR/event-processing/event-automation-welcome-urls.yaml_template |
  sed "s#{{WELCOME_URL_EVENTSTREAMS}}#placeholder#g;" | 
  sed "s#{{WELCOME_URL_EVENTENDPOINTMANAGEMENT}}#placeholder#g;" |
  sed "s#{{WELCOME_URL_EVENTPROCESSING}}#placeholder#g;" > $SCRIPT_DIR/event-processing/event-automation-welcome-urls.yaml

oc apply -f event-processing/event-automation-welcome-urls.yaml

rm event-processing/event-automation-welcome-urls.yaml

cat $SCRIPT_DIR/event-processing/event-processing.yaml_template |
  sed "s#{{NAMESPACE}}#$NAMESPACE#g;" | 
  sed "s#{{BLOCK_STORAGE}}#$BLOCK_STORAGE#g;" > $SCRIPT_DIR/event-processing/event-processing.yaml

oc apply -f event-processing/event-processing.yaml


END=$((SECONDS+3600))
EVENT_PROCESSING=FAILED
while [ $SECONDS -lt $END ]; do
    EP_PHASE=$(oc get EventProcessing ademo-ep -o=jsonpath={'..phase'})
    if [[ $EP_PHASE == "Running" ]]
    then
      echo "Event Processing available"
      EVENT_PROCESSING=SUCCESS
      break
    else
      echo "Waiting for Event Processing to be available this may take 5-10 minutes"
      sleep 60
    fi
done

rm event-processing/event-processing.yaml


if [[ $EVENT_PROCESSING == "SUCCESS" ]]
then
  echo "SUCCESS"
else
  echo "ERROR: IBM Event Processing failed to install after 60 minutes"
  exit 1
fi

EVENTSTREAMURL=$(oc get EventStreams ademo-es -o jsonpath='{..status.adminUiUrl}')
EVENTENDPOINTMANAGEMENTURL=$(oc get EventEndpointManagement ademo-eem -o jsonpath='{..status.endpoints[0].uri}')
EVENTPROCESSINGURL=$(oc get EventProcessing ademo-ep -o jsonpath='{..status.endpoints[0].uri}')

cat $SCRIPT_DIR/event-processing/event-automation-welcome-urls.yaml_template |
  sed "s#{{WELCOME_URL_EVENTSTREAMS}}#$EVENTSTREAMURL#g;" | 
  sed "s#{{WELCOME_URL_EVENTENDPOINTMANAGEMENT}}#$EVENTENDPOINTMANAGEMENTURL#g;" |
  sed "s#{{WELCOME_URL_EVENTPROCESSING}}#$EVENTPROCESSINGURL#g;" > $SCRIPT_DIR/event-processing/event-automation-welcome-urls.yaml

oc apply -f event-processing/event-automation-welcome-urls.yaml

rm event-processing/event-automation-welcome-urls.yaml

line_separator "SUCCESS - IBM EVENT PROCESSING CREATED"

echo ""
line_separator "START - INSTALLING KAFKA TOPIC"

cat $SCRIPT_DIR/event-streams/kafka-topic.yaml_template |
  sed "s#{{NAMESPACE}}#$NAMESPACE#g;" > $SCRIPT_DIR/event-streams/kafka-topic.yaml

oc apply -f event-streams/kafka-topic.yaml
END=$((SECONDS+3600))
TOPIC=FAILED
while [ $SECONDS -lt $END ]; do
    TOPIC_PHASE=$(oc get kafkatopic.eventstreams.ibm.com products -o=jsonpath={'..conditions[?(@.type=="Ready")].status'})
    if [[ $TOPIC_PHASE == "True" ]]
    then
      echo "Kafka topic available"
      TOPIC=SUCCESS
      break
    else
      echo "Waiting for Kafka topic to be available - this may take 5-10 minutes"
      sleep 60
    fi
done

line_separator "SUCCESS - KAFKA TOPIC CREATED"


echo ""
line_separator "START - INSTALLING KAFKA CONNECT"

oc apply -f event-streams/kafka-users.yaml

cat $SCRIPT_DIR/event-streams/kafka-connect.yaml_template |
  sed "s#{{NAMESPACE}}#$NAMESPACE#g;" > $SCRIPT_DIR/event-streams/kafka-connect.yaml

oc apply -f event-streams/kafka-connect.yaml
END=$((SECONDS+3600))
KAFKA_CONNECT=FAILED
while [ $SECONDS -lt $END ]; do
    CONNECT_PHASE=$(oc get kafkaconnect.eventstreams.ibm.com kafka-connect-cluster -o=jsonpath={'..conditions[?(@.type=="Ready")].status'})
    if [[ $CONNECT_PHASE == "True" ]]
    then
      echo "Kafka connect available"
      KAFKA_CONNECT=SUCCESS
      break
    else
      echo "Waiting for Kafka connect to be available - this may take 5-10 minutes"
      sleep 60
    fi
done

line_separator "SUCCESS - KAFKA CONNECT CREATED"

echo ""
line_separator "START - INSTALLING KAFKA CONNECTOR"

oc apply -f event-streams/kafka-connector-generate-load.yaml

cat $SCRIPT_DIR/event-streams/kafka-connector.yaml_template |
  sed "s#{{NAMESPACE}}#$NAMESPACE#g;" > $SCRIPT_DIR/event-streams/kafka-connector.yaml

if [ "$ENABLE_CONNECTOR" = true ] ; then


  line_separator "START - INSTALLING KAFKA ORDERS TOPIC"

  cat $SCRIPT_DIR/event-streams/kafka-topic-orders.yaml_template |
    sed "s#{{NAMESPACE}}#$NAMESPACE#g;" > $SCRIPT_DIR/event-streams/kafka-topic-orders.yaml

  oc apply -f event-streams/kafka-topic-orders.yaml
  END=$((SECONDS+3600))
  TOPIC=FAILED
  while [ $SECONDS -lt $END ]; do
      TOPIC_PHASE=$(oc get kafkatopic.eventstreams.ibm.com orders -o=jsonpath={'..conditions[?(@.type=="Ready")].status'})
      if [[ $TOPIC_PHASE == "True" ]]
      then
        echo "Kafka order topic available"
        TOPIC=SUCCESS
        break
      else
        echo "Waiting for Kafka order topic to be available - this may take 5-10 minutes"
        sleep 60
      fi
  done

  line_separator "SUCCESS - KAFKA TOPIC ORDERS CREATED"


  echo ""

  oc apply -f event-streams/kafka-connector.yaml

  END=$((SECONDS+3600))
  KAFKA=FAILED
  while [ $SECONDS -lt $END ]; do
    KAFKA_PHASE=$(oc get kafkaconnector.eventstreams.ibm.com mq-connector -o=jsonpath={'..conditions[?(@.type=="Ready")].status'})
    if [[ $KAFKA_PHASE == "True" ]]
    then
      echo "Kafka connector available"
      KAFKA=SUCCESS
      break
    else
      echo "Waiting for Kafka connector to be available - this may take 5-10 minutes"
      sleep 60
    fi
  done
fi

line_separator "SUCCESS - KAFKA CONNECTOR CREATED"

./configure-event-management.sh $NAMESPACE
./configure-event-processing.sh $NAMESPACE

app/deploy.sh $NAMESPACE

echo ""
echo ""
line_separator "User Interfaces"
PLATFORM_NAV_USERNAME=$(oc get secret integration-admin-initial-temporary-credentials -o=jsonpath={.data.username} | base64 -d)
PLATFORM_NAV_PASSWORD=$(oc get secret integration-admin-initial-temporary-credentials -o=jsonpath={.data.password} | base64 -d)
PLATFORM_NAVIGATOR_URL=$(oc get route platform-navigator-pn -o jsonpath={'.spec.host'})
echo "Platform Navigator URL: https://$PLATFORM_NAVIGATOR_URL"
echo "    username: $PLATFORM_NAV_USERNAME"
echo "    password: $PLATFORM_NAV_PASSWORD"
MQ_URL=$(oc get qmgr orders -o jsonpath='{..status.adminUiUrl}')
ES_PASSWORD=$(oc get secret es-admin -o=jsonpath='{.data.password}' | base64 -d)
echo ""
echo "Event Streams URL: $EVENTSTREAMURL"
echo "    username: es-admin"
echo "    password: $ES_PASSWORD"
echo ""
echo "Event Endpoint Management URL: $EVENTENDPOINTMANAGEMENTURL"
echo "    username: eem-admin"
echo "    password: passw0rd"
echo ""
echo "Event Processing URL: $EVENTPROCESSINGURL"
echo "    username: ep-admin"
echo "    password: passw0rd"
echo ""
echo "MQ URL: $MQ_URL"
echo "    username: $PLATFORM_NAV_USERNAME"
echo "    password: $PLATFORM_NAV_PASSWORD"
echo ""

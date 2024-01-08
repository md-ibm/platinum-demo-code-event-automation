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

if [ -z $NAMESPACE ]
then
    echo "Usage: deploy.sh <namespace for deployment> <file storage class> <block storage class>"
    exit 1
fi

oc new-project $NAMESPACE 2> /dev/null
oc project $NAMESPACE

echo ""
echo ""
EVENTSTREAMURL=$(oc get EventStreams ademo-es -o jsonpath='{..status.adminUiUrl}')
EVENTENDPOINTMANAGEMENTURL=$(oc get EventEndpointManagement ademo-eem -o jsonpath='{..status.endpoints[0].uri}')
EVENTPROCESSINGURL=$(oc get EventProcessing ademo-ep -o jsonpath='{..status.endpoints[0].uri}')

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

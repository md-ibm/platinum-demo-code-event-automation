#!/bin/bash
# © Copyright IBM Corporation 2023
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

NAMESPACE=${1:-"cp4i"}
RELEASE_NAME=${2:-"ademo"}
ORG_NAME=${3:-"ibm-demo"}

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

if [[ $OSTYPE == 'darwin'* ]]; then
  echo 'Running on macOS'
  EP_ROLES=$(cat $SCRIPT_DIR/event-processing/ep-roles.json | base64)
EP_USERS=$(cat $SCRIPT_DIR/event-processing/ep-users.json | base64)
else
  EP_ROLES=$(cat $SCRIPT_DIR/event-processing/ep-roles.json | base64 -w0)
  EP_USERS=$(cat $SCRIPT_DIR/event-processing/ep-users.json | base64 -w0)
fi

RESPONSE=$(oc patch secret ademo-ep-ibm-ep-user-credentials -n $NAMESPACE -p '{"data": {"user-credentials.json": "'$EP_USERS'"}}')
echo Patched ademo-ep-ibm-ep-user-credentials $RESPONSE

RESPONSE=$(oc patch secret ademo-ep-ibm-ep-user-roles -n $NAMESPACE -p '{"data": {"user-mapping.json": "'$EP_ROLES'"}}')
echo Patched ademo-ep-ibm-ep-user-roles $RESPONSE
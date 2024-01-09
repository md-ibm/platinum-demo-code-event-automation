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
  EEM_ROLES=$(cat $SCRIPT_DIR/resources/eem-roles.json | base64)
  EEM_USERS=$(cat $SCRIPT_DIR/resources/eem-users.json | base64)
else
  EEM_ROLES=$(cat $SCRIPT_DIR/resources/eem-roles.json | base64 -w0)
  EEM_USERS=$(cat $SCRIPT_DIR/resources/eem-users.json | base64 -w0)
fi

RESPONSE=$(oc patch secret ademo-eem-ibm-eem-user-credentials -n $NAMESPACE -p '{"data": {"user-credentials.json": "'$EEM_USERS'"}}')
echo Patched ademo-eem-ibm-eem-user-credentials $RESPONSE

RESPONSE=$(oc patch secret ademo-eem-ibm-eem-user-roles -n $NAMESPACE -p '{"data": {"user-mapping.json": "'$EEM_ROLES'"}}')
echo Patched ademo-eem-ibm-eem-user-roles $RESPONSE
#!/usr/bin/env bash
#
# Copyright 2015 ICT.
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


# This script loads netflow-env.sh if it exists, and ensures it is only loaded once.
# netflow-env.sh is loaded from NETFLOW_CONF_DIR if set, or within the current directory's
# conf/ subdirectory.

if [ -z "$NETFLOW_ENV_LOADED" ]; then
  export SPARK_ENV_LOADED=1

  # Returns the parent of the directory this script lives in.
  parent_dir="$(cd "`dirname "$0"`"/..; pwd)"

  user_conf_dir="${NETFLOW_CONF_DIR:-"$parent_dir"/conf}"

  if [ -f "${user_conf_dir}/netflow-env.sh" ]; then
    # Promote all variable declarations to environment (exported) variables
    set -a
    . "${user_conf_dir}/netflow-env.sh"
    set +a
  fi
fi

# Setting SPARK_SCALA_VERSION if not already set.

#if [ -z "$NETFLOW_SCALA_VERSION" ]; then
#
#    ASSEMBLY_DIR2="$FWDIR/assembly/target/scala-2.11"
#    ASSEMBLY_DIR1="$FWDIR/assembly/target/scala-2.10"
#
#    if [[ -d "$ASSEMBLY_DIR2" && -d "$ASSEMBLY_DIR1" ]]; then
#        echo -e "Presence of build for both scala versions(SCALA 2.10 and SCALA 2.11) detected." 1>&2
#        echo -e 'Either clean one of them or, export SPARK_SCALA_VERSION=2.11 in spark-env.sh.' 1>&2
#        exit 1
#    fi
#
#    if [ -d "$ASSEMBLY_DIR2" ]; then
#        export SPARK_SCALA_VERSION="2.11"
#    else
#        export SPARK_SCALA_VERSION="2.10"
#    fi
#fi

export NETFLOW_SCALA_VERSION="2.10"
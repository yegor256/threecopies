#!/usr/bin/env bash

container=$1
minutes=$2

cd "${container}"
docker rm -f "${container}"
echo "The script was killed because it was taking too long (${minutes} minutes)" >> log

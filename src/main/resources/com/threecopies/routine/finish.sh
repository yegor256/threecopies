#!/usr/bin/env bash

container=$1

cd "${container}"
if [ -e exit ]; then
    cat exit
    cat log
    cd ..
    rm -rf "${container}"
else
    ls -al log
fi

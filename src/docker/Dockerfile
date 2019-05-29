# The MIT License (MIT)
#
# Copyright (c) 2017 Yegor Bugayenko
#
# Permission is hereby granted, free of charge,  to any person obtaining
# a copy  of  this  software  and  associated  documentation files  (the
# "Software"),  to deal in the Software  without restriction,  including
# without limitation the rights to use,  copy,  modify,  merge, publish,
# distribute,  sublicense,  and/or sell  copies of the Software,  and to
# permit persons to whom the Software is furnished to do so,  subject to
# the  following  conditions:   the  above  copyright  notice  and  this
# permission notice  shall  be  included  in  all copies or  substantial
# portions of the Software.  The software is provided  "as is",  without
# warranty of any kind, express or implied, including but not limited to
# the warranties  of merchantability,  fitness for  a particular purpose
# and non-infringement.  In  no  event shall  the  authors  or copyright
# holders be liable for any claim,  damages or other liability,  whether
# in an action of contract,  tort or otherwise,  arising from, out of or
# in connection with the software or  the  use  or other dealings in the
# software.

FROM ubuntu:16.04
MAINTAINER Yegor Bugayenko <yegor256@gmail.com>
LABEL Description="This is the default image for ThreeCopies.com" Vendor="ThreeCopies.com" Version="1.0"
WORKDIR /root

ENV DEBIAN_FRONTEND=noninteractive

# Unicode
ENV LC_ALL en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US.UTF-8

# Basic Linux tools
RUN apt-get update
RUN apt-get install -y wget bcrypt curl
RUN apt-get install -y unzip zip
RUN apt-get install -y gnupg gnupg2
RUN apt-get install -y bsdmainutils
RUN apt-get install -y libxml2-utils
RUN apt-get install -y build-essential
RUN apt-get install -y automake autoconf

# Git 2.0
RUN apt-get install -y software-properties-common python-software-properties
RUN add-apt-repository ppa:git-core/ppa
RUN apt-get update
RUN apt-get install -y git git-core

# PHP
RUN add-apt-repository ppa:ondrej/php \
  && apt-get install -y php7.2

# Java
RUN apt-get install -y default-jdk

# MongoDB
RUN sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 0C49F3730359A14518585931BC711F9BA15703C6 \
  && echo "deb [ arch=amd64 ] http://repo.mongodb.org/apt/ubuntu trusty/mongodb-org/3.4 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.4.list \
  && sudo apt-get update \
  && sudo apt-get install -y mongodb-org

# S3cmd for AWS S3 integration
RUN apt-get install -y s3cmd

# MySQL client
RUN apt-get install -y mysql-client

# PostgreSQL client
RUN apt-get install wget ca-certificates \
  && wget -q https://www.postgresql.org/media/keys/ACCC4CF8.asc -O - | sudo apt-key add - \
  && echo "deb http://apt.postgresql.org/pub/repos/apt/ `lsb_release -cs`-pgdg main" >> /etc/apt/sources.list.d/pgdg.list \
  && sudo apt-get update \
  && apt-get install -y postgresql postgresql-contrib

# NodeJS
RUN rm -rf /usr/lib/node_modules
RUN curl -sL https://deb.nodesource.com/setup_6.x | bash -
RUN apt-get install -y nodejs

# Ruby
RUN apt-get update && apt-get install -y ruby-dev libmagic-dev zlib1g-dev
RUN gpg2 --keyserver hkp://keys.gnupg.net --recv-keys D39DC0E3
RUN curl -L https://get.rvm.io | bash -s stable
RUN /bin/bash -l -c ". /etc/profile.d/rvm.sh && rvm install 2.3.3 && gem update && gem install --no-ri --no-rdoc nokogiri bundler"

# Clean up
RUN rm -rf /tmp/*
RUN rm -rf /root/.ssh

ENTRYPOINT ["/bin/bash", "-l", "-c"]

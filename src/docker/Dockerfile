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

FROM ubuntu:14.04
MAINTAINER Yegor Bugayenko <yegor256@gmail.com>
LABEL Description="This is the default image for ThreeCopies.com" Vendor="ThreeCopies.com" Version="1.0"
WORKDIR /root

ENV DEBIAN_FRONTEND=noninteractive

# UTF-8 locale
RUN locale-gen en_US en_US.UTF-8
RUN dpkg-reconfigure locales
ENV LC_ALL en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US.UTF-8

RUN apt-get update \
    && echo "Installation: Basic Linux tools" \
    && apt-get install -y wget bcrypt curl \
    && apt-get install -y unzip zip \
    && apt-get install -y gnupg gnupg2 \
    && apt-get install -y bsdmainutils \
    && apt-get install -y libxml2-utils \
    && apt-get install -y build-essential \
    && apt-get install -y automake autoconf \
    && echo "Installation: Git 2.0" \
    && apt-get install -y software-properties-common python-software-properties \
    && add-apt-repository ppa:git-core/ppa \
    && apt-get update \
    && apt-get install -y git git-core \
    && echo "Installation: Ruby" \
    && apt-get update \
    && apt-get install -y ruby-dev libmagic-dev=1:5.14-2ubuntu3.3 zlib1g-dev=1:1.2.8.dfsg-1ubuntu1 \
    && gpg2 --keyserver hkp://keys.gnupg.net --recv-keys D39DC0E3 \
    && curl -L https://get.rvm.io | bash -s stable \
    && /bin/bash -l -c "rvm requirements" \
    && /bin/bash -l -c "rvm install 2.3.3" \
    && /bin/bash -l -c "gem update && gem install --no-ri --no-rdoc nokogiri:1.6.7.2 bundler:1.11.2" \
    && echo "Installation: PHP" \
    && apt-get install -y php5 php5-dev php-pear \
    && echo "Installation: Java" \
    && apt-get install -y default-jdk \
    && echo "Installation: S3cmd for AWS S3 integration" \
    && apt-get install -y s3cmd \
    && echo "Installation: MySQL client" \
    && apt-get install -y mysql-client \
    && echo "Installation: NodeJS" \
    && rm -rf /usr/lib/node_modules \
    && curl -sL https://deb.nodesource.com/setup_6.x | bash - \
    && apt-get install -y nodejs \
    && echo "Installation: Clean up" \
    && apt-get clean \
    && rm -rf /tmp/* /root/.ssh /var/lib/apt/lists/* /var/tmp/*
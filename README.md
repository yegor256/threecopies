<img src="src/main/resources/images/logo.png" width="64px" height="64px" alt="ThreeCopies logo"/>

[![Managed by Zerocracy](https://www.0crat.com/badge/C3RFVLU72.svg)](https://www.0crat.com/p/C3RFVLU72)
[![DevOps By Rultor.com](http://www.rultor.com/b/yegor256/threecopies)](http://www.rultor.com/p/yegor256/threecopies)

[![Availability at SixNines](https://www.sixnines.io/b/5c94)](https://www.sixnines.io/h/5c94)

[![Build Status](https://travis-ci.org/yegor256/threecopies.svg)](https://travis-ci.org/yegor256/threecopies)
[![PDD status](http://www.0pdd.com/svg?name=yegor256/threecopies)](http://www.0pdd.com/p?name=yegor256/threecopies)
[![Test Coverage](https://img.shields.io/codecov/c/github/yegor256/threecopies.svg)](https://codecov.io/github/yegor256/threecopies?branch=master)

## What does it do?

[ThreeCopies.com](http://www.threecopies.com) is a hosted service that
regularly archives your server-side resources. We create three
copies: hourly, daily and weekly.

What's interesting is that the entire product <del>is</del> will be written in [EO](http://www.eolang.org),
a truly object-orented programming language.

The logo is made by [Freepik](http://www.freepik.com) from [flaticon.com](http://www.flaticon.com),
licensed by [CC 3.0 BY](http://creativecommons.org/licenses/by/3.0/).

## How to configure

Each script is a bash scenario, which you design yourself. ThreeCopies
just starts it regularly and records its output. These are some
recommendations on how to design the script. There are three parts:
input, package, and output. First, you collect some data from your data
sources (input). Then, you compress and encrypt the data (package). Finally,
you store the package somewhere (output).

We start your script inside
[yegor256/threecopies](https://hub.docker.com/r/yegor256/threecopies/)
Docker container,
here is the
[`Dockerfile`](https://github.com/yegor256/threecopies/blob/master/src/docker/Dockerfile).

If you don't want your script to be executed too frequently, you may put
this code in front of it (to skip hourly executions, for example):

```bash
if [ "${period}" == "hour" ]; then exit 0; fi
```

### 1. Input

To retrieve the data from a MySQL database use [mysqldump](https://dev.mysql.com/doc/refman/5.7/en/mysqldump.html):

```bash
mysqldump --lock-tables=false --host=www.example.com \
  --user=username --password=password \
  --databases dbname > mysql.sql
```
Since this would require to open your mysql port to the internet, which is not advisable from a security perspective, you should probably use a ssh tunnel:

```bash
cat > file.key <<EOT
-----BEGIN RSA PRIVATE KEY-----
<your ssh private key here>
-----END RSA PRIVATE KEY-----
EOT
chmod 700 file.key
ssh -Nf -i file.key -L3306:localhost:3306 your_user@www.example.com
rm file.key
```
and then connect with the above script:

```bash
mysqldump --lock-tables=false --host=localhost ...same as above
```

To download an entire FTP directory use [wget](https://www.gnu.org/software/wget/):

```bash
wget --mirror --tries=5 --quiet --output-file=/dev/null \
  --ftp-user=username --ftp-password=password \
  ftp://ftp.example.com/some-directory
```

### 2. Package

To package a directory use [tar](https://help.ubuntu.com/community/BackupYourSystem/TAR):

```bash
tgz="${period}-$(date "+%Y-%m-%d-%H-%M").tgz"
tar czf "${tgz}" some-directory
```

We recommend to use exactly that name of your `.tgz` archives. The
`${period}` environment variable is provided by our server to your
Docker container, it will either be set to `hour`, `day`, or `week`.

### 3. Output

To upload a file to Amazon S3, using [s3cmd](http://s3tools.org/s3cmd):

```bash
echo "[default]" > ~/.s3cfg
echo "access_key=AKIAICJKH*****CVLAFA" >> ~/.s3cfg
echo "secret_key=yQv3g3ao654Ns**********H1xQSfZlTkseA0haG" >> ~/.s3cfg
s3cmd --no-progress put "${tgz}" "s3://backup.example.com/${tgz}"
```

## DynamoDB Schema

The `tc-scripts` table contains all registered scripts:

```
fields:
  login/H: GitHub login of the owner
  name/R: Unique name of the script
  bash: Bash script
  hour: Epoch-sec when it recent hourly log was scheduled
  day: Epoch-sec when it recent daily log was scheduled
  week: Epoch-sec when it recent weekly log was scheduled
```

The `tc-logs` table contains all recent logs:

```
fields:
  group/H: Concatenated GitHub login and script name, e.g. "yegor256/test"
  finish/R: Epoch-msec of the script finish (or MAX_LONG if still running)
  login: GitHub login of the owner
  period: Either "hour", "day", or "week"
  ocket: S3 object name for the log
  ttl: Epoch-sec when the record has to be deleted (by DynamoDB)
  start: Epoch-msec time of the start
  container: Docker container name
  exit: Bash exit code (error if not zero)
mine (index):
  login/H
  finish/R
```

## How to contribute?

Just submit a pull request. Make sure `mvn` passes.

## License

(The MIT License)

Copyright (c) 2017-2020 Yegor Bugayenko

Permission is hereby granted, free of charge,  to any person obtaining
a copy  of  this  software  and  associated  documentation files  (the
"Software"),  to deal in the Software  without restriction,  including
without limitation the rights to use,  copy,  modify,  merge, publish,
distribute,  sublicense,  and/or sell  copies of the Software,  and to
permit persons to whom the Software is furnished to do so,  subject to
the  following  conditions:   the  above  copyright  notice  and  this
permission notice  shall  be  included  in  all copies or  substantial
portions of the Software.  The software is provided  "as is",  without
warranty of any kind, express or implied, including but not limited to
the warranties  of merchantability,  fitness for  a particular purpose
and non-infringement.  In  no  event shall  the  authors  or copyright
holders be liable for any claim,  damages or other liability,  whether
in an action of contract,  tort or otherwise,  arising from, out of or
in connection with the software or  the  use  or other dealings in the
software.

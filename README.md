<!-- <img src="http://www.threecopies.com/images/logo.png" width="64px" height="64px"/> -->

[![DevOps By Rultor.com](http://www.rultor.com/b/yegor256/threecopies)](http://www.rultor.com/p/yegor256/threecopies)

[![Availability at SixNines](http://www.sixnines.io/b/9d56)](http://www.sixnines.io/h/9d56)

[![Build Status](https://travis-ci.org/yegor256/threecopies.svg)](https://travis-ci.org/yegor256/threecopies)
[![PDD status](http://www.0pdd.com/svg?name=yegor256/threecopies)](http://www.0pdd.com/p?name=yegor256/threecopies)
[![Dependency Status](https://gemnasium.com/yegor256/threecopies.svg)](https://gemnasium.com/yegor256/threecopies)
[![Code Climate](http://img.shields.io/codeclimate/github/yegor256/threecopies.svg)](https://codeclimate.com/github/yegor256/threecopies)
[![Test Coverage](https://img.shields.io/codecov/c/github/yegor256/threecopies.svg)](https://codecov.io/github/yegor256/threecopies?branch=master)

## What does it do?

[ThreeCopies.com](http://www.threecopies.com) is a hosted service that
regularly archives your server-side resources. We create three
copies: hourly, daily and weekly.

What's interesting is that the entire product is written in [EO](http://www.eolang.org),
a truly object-orented programming language.

The logo is made by [Freepik](http://www.freepik.com) from [flaticon.com](http://www.flaticon.com),
licensed by [CC 3.0 BY](http://creativecommons.org/licenses/by/3.0/).

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

Copyright (c) 2017 Yegor Bugayenko

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

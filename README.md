page2feed
=========

Generate Atom feeds with public pages modifications.

## How to run

- install a JDK
- setup a PostgreSQL database
- add a DB access to `conf/application.conf` or override some of its keys while lauching (see http://www.playframework.com/documentation/2.3.x/ProductionConfiguration)
- `./activator run` (dev) or `./activator stage` and then `./target/universal/stage/bin/page2feed` (prod)

## How to use

- get the URL to monitor
- put it after `http://yourserver/feed/` (like `http://yourserver/feed/http://www.wikipedia.org`)
- add the resulting URL in your feed aggregator

The URL to monitor will be fetched every time your aggregator fetches the feed.
So your aggregator determines the monitoring frequency.

## License

The MIT License (MIT) Copyright (c) 2015 David Sferruzza

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

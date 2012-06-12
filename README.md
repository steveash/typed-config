typed-config
============

Strongly typed configuration layer on top of apache commons configuration.

(This was originally a fork from http://code.google.com/p/config-proxy/ by Jonny Tyers, but I needed some 
additional features which resulted in a very different code base.  As I need an issue tracker myself to 
manage my own features for the current project that is driving this, I decided to go ahead and move to GitHub
for familiarity.  I hope that we can reconcile our code bases at some point in the future, but until then, this
is where I am doing my development).

using typed-config
============
typed-config isn't really read for prime-time yet, but you can start using the snapshots by pulling them from my
github maven repo:

```
<repositories>
  <repository>
    <id>steveash-snapshots</id>
    <url>https://github.com/steveash/mvn-repo/raw/master/snapshots</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.steveash</groupId>
    <artifactId>typed-config</artifactId>
    <version>0.2-SNAPSHOT</version>
  </dependency>
</dependencies>
```

license
============

Copyright (c) 2012 Jonathan Tyers, Steve Ash

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
[![Build Status](https://drone.io/github.com/sdcote/commons/status.png)](https://drone.io/github.com/sdcote/commons/latest)
[ ![Download](https://api.bintray.com/packages/sdcote/maven/Commons/images/download.svg)](https://bintray.com/sdcote/maven/Commons/_latestVersion)

Coyote Commons
==============

A library of utility classes gathered since the early days of Java 1.

This is a library of various utility classes to make life a bit easier. It was developed over the past 15+ years of working on various projects and represents a collection of helpful routines created since Java 1.1, many of which have since been included in one form or another in either the Java API set or some other open source project.

Sure there is probably a function in this collection that is already provided by another commons project, but I/we were just not interested in adding another dependency to our project just for a function or two.

This library will grow as I go through my code libraries and find useful functions and classes in other projects. 

Security
--------
There are several security related classes in this package including encryption utilities and a security context model to implement Role Based Access Control (RBAC) in any Java component. Multiple security contexts can be supported and the permissions model is extensible.
 
These security classes form the building blocks of more secure components and require their proper employment at the application level to secure a component or application.

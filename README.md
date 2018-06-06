# javaxsx

Tools and utilities in Java.

## Usage

### Maven dependency:

```
<dependency>
    <groupId>com.eroelf</groupId>
    <artifactId>javaxsx</artifactId>
    <version>${version}</version>
</dependency>
```

### Manual

#### behavior

This package provides some basic definition for an event happened at a specified time, say, *History*.

If the *History* is related to a subjective action, it becomes a *Behavior*. As many web sites are concerned, users is a basic dimension, so there comes *UserBehavior*.

*Behaviors* class defines some basic methods to deal with behaviors.

#### db

Provides some simple APIs for database requests. There is **NO** support for connection pools and standing connections.

For example:

##### Simple query

```java
DoDb doDb=new DoDb();
doDb.setConnection(connection);
doDb.executeQuery(true, "select ...");
doDb.executeUpdate(false, "insert into ...", obj1, obj2, ...);
doDb.close();
```

##### Batch

```java
doDb.prepareStatement("insert into person (id, name, age, learning_subject) values (?, ?, ?, ?)");
while(...)
{
    ...
    String id=...
    String name=...
    int age=...
    String learning_subject=...
    doDb.addBatch(id, name, age, learning_subject);
    ...
}
doDb.executeBatch();
```

##### Convert to list

```java
public class Person
{
    public String id;
    public String name;
    public int age;
    public String learningSubject;
}

List<Person> list=doDb.fromQuery(Person.class, true, true, "select id, name, age, learning_subject from person");
```

#### geo

Provides some simple API for geography.

##### Coordinate convert

```java
System.out.println(CoordinateConverter.fromWGS84ToGCJ02(39.9671454379, 116.3281085168));
System.out.println(CoordinateConverter.fromGCJ02ToWGS84(CoordinateConverter.fromWGS84ToGCJ02(39.9671454379, 116.3281085168)));
System.out.println(CoordinateConverter.fromWGS84ToBD09(39.9684488626, 116.3342346822));
System.out.println(CoordinateConverter.fromBD09ToGCJ02(39.97411842293451, 116.34085726246536));

//////// output ////////
\> (39.96845939748989, 116.3342501022455, NaN)
\> (39.9671336261829, 116.32809425470623, NaN)
\> (39.975428838226456, 116.34701964033485, NaN)
\> (39.968449623723544, 116.33423475270888, NaN)
```

##### Ground distance

```java
System.out.println(GeoUtil.distance(39.2635, 116.8098, 39.6572, 116.3422));

//////// output ////////
\> 59414.14058762891
```

#### group

This package provides APIs for grouping some certain set of objects, including methods for configuring, updating from some source, and schedule auto-updating.

Simple usage

```java
GroupingUtil.configFacet("GROUPING_TEST", "_grouping_test", "A,B,C,Z".split(","), "0.25,0.75,1".split(","), (id) -> true, new Md5HashGetter(2));
Group group=GroupingUtil.getGroupFromIdentifier("GROUPING_TEST", "390QSJPOSFN0543XF0WJ");
System.out.println(group.getFacetName());
System.out.println(group.getGroupName());

//////// output ////////
\> GROUPING_TEST
\> A
```

One may like to delegate a *Group* object to apply a special strategy on objects under this group. Just assign a *GroupTask* functional interface to the Group object.

Classes in package updater are helpful for update grouping configures from a specified source, e.g., a database.

#### index

APIs for a simple form to index a branch of objects, implemented by using *HashMap*.

#### io

##### Reading files

```java
String fileName="data.gz";
new FileReader(fileName).lines();
FileReaderPlus.readAFile(fileName, (line) -> {
    ...
    return true;
});
new FileIterator<String>(fileName).lines();
new DirFileIterator<String>("/").lines();
```

Support *text*, *gzip*, *bzip2*, and *zip* format. Extend *InputHelper* class and override *getCompressType* and *convert* methods to modify compress type judgment and to support more file types.

##### Compressor

The *Compressor* class provides methods to compress and decompress data. Override *CompressorBuilder::setCompression* method to support more compress types.

#### math

Some mathematics related methods. Such as mapping, combination, and statistics.

* Examples of using class *DataPreProcessor*:

  * Basic statistics and scaling

```java
DataPreProcessor dataPreProcessor=new DataPreProcessor(new double[]{2, 4, 4, 6}, false);
System.out.println(dataPreProcessor.getOriginalMean());
System.out.println(dataPreProcessor.getOriginalStd());
System.out.println(dataPreProcessor.getOriginalSum());
System.out.println(dataPreProcessor.getOriginalMax());
System.out.println(dataPreProcessor.getOriginalMin());
System.out.println("--------------------------------");
dataPreProcessor.scale(2, 1);
dataPreProcessor.toUniform();
System.out.println(dataPreProcessor.getProcessedMean());
System.out.println(dataPreProcessor.getProcessedStd());
System.out.println(dataPreProcessor.getProcessedSum());
System.out.println(dataPreProcessor.getProcessedMax());
System.out.println(dataPreProcessor.getProcessedMin());
System.out.println("--------------------------------");
dataPreProcessor.reScale(2, 1);
dataPreProcessor.reScale(-0.5, -1);
System.out.println(dataPreProcessor.getProcessedMean());
System.out.println(dataPreProcessor.getProcessedStd());
System.out.println(dataPreProcessor.getProcessedSum());
System.out.println(dataPreProcessor.getProcessedMax());
System.out.println(dataPreProcessor.getProcessedMin());

//////// output ////////
\> 4.0
\> 1.632993161855452
\> 16.0
\> 6.0
\> 2.0
\> --------------------------------
\> 0.5
\> 0.30618621784789724
\> 2.0
\> 0.875
\> 0.125
--------------------------------
\> -2.0
\> 0.30618621784789724
\> -8.0
\> -1.625
\> -2.375
```

  * Histogram for console

```java
DataPreProcessor dataPreProcessor=new DataPreProcessor(new double[]{2, 4, 4, 6}, false);
HistInfo histInfo=dataPreProcessor.getOriginalHistogram(new double[]{0.5, 1.5, 2.5, 3.5, 4.5, 5.5, 6.5}, true);
String histStr=DataPreProcessor.formatHistogram(histInfo, 100);
System.out.println(histStr);

//////// output ////////
\> [0.5, 1.5)|0.00000000|
\> [1.5, 2.5)|0.25000000|>>>>>>>>>>>>>>>>>>>>>>>>>
\> [2.5, 3.5)|0.00000000|
\> [3.5, 4.5)|0.50000000|>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
\> [4.5, 5.5)|0.00000000|
\> [5.5, 6.5)|0.25000000|>>>>>>>>>>>>>>>>>>>>>>>>>
\>     others|0.00000000|
```

#### ml

This package is an abstract core working flow of any certain recall, recommendation, estimating, scoring, and ranking system.

There is a [demo](https://github.com/xiaoshenxian/modeling_demo) for simple usage.

* feature package

For *Item* feature storing and scoring.

  * model package
  
  A *Modelable* object can be "modeled", say, filling features, by one or more *Modeler* objects. That means the *Modelable* object contains features of a specified thing while those *Modeler* objects can be anything like strategies or descriptors to describe the *Modelable* object.
  
  * score package
  
  A *Scoreable* object can be "scored" by a *Scorer* object and hold the final score until next modification. *Scoreable* objects are *Comparable* by their scores in descending order, which implies the higher the score the "better" the *Scoreable* object is. This is consistent to general ranking and recommendation projects.
  
  * strategy package
  
  A *Strategy* object is a *Modeler* object but with method to recall candidates under its definition.
  
An *Item* object is both a *Modelable* and a *Scoreable* object. This means an *Item* object is very suitable to represent a specified "product" in a well defined ranking and recommendation system.

* flow package

This package defines general working flows for ranking and recommendation systems in both enumerable candidate situation and innumerable candidate situation. Note that innumerable candidate situation can also be used in enumerable candidate situation.

  * controller package
  
  This package defines many detailed behaviors of both the two working flow mentioned above.
  
  * convert package
  
  Provide a *Converter* class for converting a back-end product data structure (*Item*) list to a front-end product data structure (*Info*) list, as well as logging necessary information for each *Info* object to a logger data structure (*InfoLog*).
  
  * estimate package
  
  The top-most interface for both the enumerable and the innumerable candidate working flow. Call the *generate* method to get the scored but unordered product (*Item*) list. Using *Collections.sort* to sort the list if necessary.
  
  Please note that **in most cases the _generate_ method need NOT to be overridden**.
  
  * Item, Info, and InfoLog
  
  As described above, objects of all the three classes are "products" in a specified ranking and recommendation system. *Item* class is used for back-end data, *Info* class is used for front-end data, and *InfoLog* class is used for logging necessary information for *Info* objects.

#### net

Provide APIs for constructing, parsing, and requesting URLs.

##### *ParamInfo* class

Help to repeatedly construct URLs for a specified domain and pre-defined parameters with different values. See the Javadoc for detail.

##### *UrlUtil* class

Help to construct and parse URLs.

##### *RequestUrl* class

Help to request URLs. There is **NO** support for connection pools and standing connections.

#### trie

Implemented *HashMapTrie* and *DoubleArrayTrie*, and a simple trie division algorithm in *IndexedTrie*.

#### others

##### *Strings*

##### *CollectionUtil*

##### *FileSysUtil*

##### *PackageUtil*

##### *NumBytes*

##### *TimeLasted*

## Authors

* **ZHONG Weikun**

## License

This project is released under the [Apache license 2.0](LICENSE).

```
Copyright 2018 ZHONG Weikun.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

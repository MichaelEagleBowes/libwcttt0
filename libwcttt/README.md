# libwcttt

WCT³ (WIAI Course Timetabling Tool) is a software that strives to automate the 
timetabling process at the WIAI faculty of the University of Bamberg. It was 
developed by [Nicolas Gross](https://github.com/nicolasgross) as part of his 
bachelor thesis at the Software Technologies Research Group (SWT).

This library bundles functionality that is required in both other modules,
WCT³-CLI and WCT³-GUI. Moreover, it provides a solid foundation for future work
on the topic. The library comprises the data model, a binder (parser + emitter)
to store the data as XML files, the implementations of the algorithms as well as
functionality to calculate conflicts and their violations. 


## Notice

Because of an unresolved issue in the JAXB framework, warning messages are
printed to stdout if a XML file is parsed/written. The corresponding issue on 
GitHub can be found [here](https://github.com/javaee/jaxb-v2/issues/1197).


## Build

### Requirements

- Oracle JDK 10
- maven

### Steps

1. `cd [PATH_TO_PROJECT_ROOT]`
2. `mvn clean package` 
3. Optional, installs libwcttt in the local maven repository which is required
to build WCT³-CLI and WCT³-GUI.  
`mvn install`


## Usage

After the library was installed to the local maven repository, it can be used in
a maven project by adding the following dependency:

```xml
<dependencies>
    ...
    <dependency>
        <groupId>de.nicolasgross.wcttt</groupId>
        <artifactId>libwcttt</artifactId>
        <version>[VERSION]</version>
    </dependency>
    ...
</dependencies>
```

## License

This software is released under the terms of the GPLv3.

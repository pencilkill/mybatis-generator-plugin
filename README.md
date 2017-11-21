Mybatis Generator Custom Plugins (XMLMAPPER)
=

Maven plugin dependency :

    <dependency>
        <groupId>com.nd.component.java</groupId>
        <artifactId>mybatis-generator-plugin</artifactId>
        <version>${mybatis-generator-plugin-version}</version>
    </dependency>

Generic
-
Maven dependency :

    <dependency>
        <groupId>com.nd.component.java</groupId>
        <artifactId>mybatis-generic</artifactId>
        <version>${mybatis-generic-version}</version>
    </dependency>

Mybatis generator config :

    <plugin type="com.nd.mybatis.generator.plugin.ExamplePageablePlugin">
        <property name="limitStartName" value="limitStart" />
        <property name="limitEndName" value="limitEnd" />
    </plugin>
    <plugin type="com.nd.mybatis.generator.plugin.ExampleGroupByClausePlugin" />
    <plugin type="com.nd.mybatis.generator.plugin.GenericPlugin">
        <property name="servicePackage" value="com.xxx.service" />
        <property name="serviceImplPackage" value="com.xxx.service.impl" />
        <property name="beanAware" value="true" /> <!-- mapper annotated @Autowired, serviceImpl annotated @Service -->
    </plugin>


ExampleRelocate
-
Mybatis generator config :

    <plugin type="com.nd.mybatis.generator.plugin.ExampleRelocatePlugin">
        <property name="package" value="com.xxx.dao.criteria" />
    </plugin>


ExamplePageable
-
Mybatis generator config :

    <plugin type="com.nd.mybatis.generator.plugin.ExamplePageablePlugin">
        <property name="limitStartName" value="limitStart" /> <!-- optional -->
        <property name="limitEndName" value="limitEnd" /> <!-- optional -->
    </plugin>


ExampleGroupByCause
-
Mybatis generator config :

    <plugin type="com.nd.mybatis.generator.plugin.ExampleGroupByClausePlugin" />


InsertMultipleSelective
-
Mybatis generator config :

    <plugin type="com.nd.mybatis.generator.plugin.InsertMultipleSelectivePlugin" />
    
ExampleCriteriaWhereClause
-
Mybatis generator config :

    <plugin type="com.nd.mybatis.generator.plugin.ExampleCriteriaWhereClausePlugin" />
    
Reference [Mybatis Generator Plugins](http://www.mybatis.org/generator/reference/pluggingIn.html)

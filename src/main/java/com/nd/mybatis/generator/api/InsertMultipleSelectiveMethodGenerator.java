/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.mybatis.generator.api;

import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

import com.nd.mybatis.generator.plugin.InsertMultipleSelectivePlugin;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public class InsertMultipleSelectiveMethodGenerator extends AbstractJavaMapperMethodGenerator
{

    public InsertMultipleSelectiveMethodGenerator()
    {
        super();
    }

    @Override
    public void addInterfaceElements(Interface interfaze)
    {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = new Method();

        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(InsertMultipleSelectivePlugin.ATTR_INSERT_MULTIPLE_SELECTIVE_STATEMENT_ID);

        FullyQualifiedJavaType parameterType = introspectedTable.getRules().calculateAllFieldsClass();
        FullyQualifiedJavaType parameterTypes = FullyQualifiedJavaType.getNewListInstance();
        
        parameterTypes.addTypeArgument(parameterType);

        importedTypes.add(parameterType);
        importedTypes.add(parameterTypes);
        
        method.addParameter(new Parameter(parameterType, "record", "@Param(\"record\")"));
        method.addParameter(new Parameter(parameterTypes, "records", "@Param(\"records\")"));
        
        importedTypes.add(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));
        
        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);

        addMapperAnnotations(interfaze, method);

        if (context.getPlugins().clientInsertSelectiveMethodGenerated(method, interfaze, introspectedTable))
        {
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }

    public void addMapperAnnotations(Interface interfaze, Method method)
    {
        return;
    }
}

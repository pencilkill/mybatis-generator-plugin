/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.mybatis.generator.plugin;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import com.nd.mybatis.generator.api.InsertMultipleSelectiveMethodGenerator;
import com.nd.mybatis.generator.dom.InsertMultipleSelectiveElementGenerator;
import com.nd.mybatis.generator.util.JavaElementUtil;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public class InsertMultipleSelectivePlugin extends PluginAdapter
{
    public static final String ATTR_INSERT_MULTIPLE_SELECTIVE_STATEMENT_ID = "insertMultipleSelective";

    /**
     * @param warnings
     * @return
     * @see org.mybatis.generator.api.Plugin#validate(java.util.List)
     */
    @Override
    public boolean validate(List<String> warnings)
    {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
    {
        AbstractJavaMapperMethodGenerator methodGenerator = new InsertMultipleSelectiveMethodGenerator();

        methodGenerator.setContext(context);
        methodGenerator.setIntrospectedTable(introspectedTable);
        methodGenerator.addInterfaceElements(interfaze);

        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable)
    {
        int index = JavaElementUtil.xmlElementIndex(document, "insert", new Attribute("id", introspectedTable.getInsertSelectiveStatementId()));
        
        if(index >= 0)
        {
            AbstractXmlElementGenerator elementGenerator = new InsertMultipleSelectiveElementGenerator(index + 1);
            
            elementGenerator.setContext(context);
            elementGenerator.setIntrospectedTable(introspectedTable);
            elementGenerator.addElements(document.getRootElement());
        }
        
        return true;
    }
}

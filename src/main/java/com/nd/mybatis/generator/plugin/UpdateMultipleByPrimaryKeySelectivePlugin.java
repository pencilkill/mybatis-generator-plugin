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

import com.nd.mybatis.generator.api.UpdateMultipleByPrimaryKeySelectiveMethodGenerator;
import com.nd.mybatis.generator.dom.UpdateMultipleByPrimaryKeySelectiveElementGenerator;
import com.nd.mybatis.generator.util.ElementUtil;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public class UpdateMultipleByPrimaryKeySelectivePlugin extends PluginAdapter
{
    public static final String ATTR_UPDATE_MULTIPLE_BY_PRIMARY_KEY_SELECTIVE_STATEMENT_ID = "updateMultipleByPrimaryKeySelective";

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
        AbstractJavaMapperMethodGenerator methodGenerator = new UpdateMultipleByPrimaryKeySelectiveMethodGenerator();

        methodGenerator.setContext(context);
        methodGenerator.setIntrospectedTable(introspectedTable);
        methodGenerator.addInterfaceElements(interfaze);

        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable)
    {
        int index = ElementUtil.xmlElementIndex(document, "update", new Attribute("id", introspectedTable.getUpdateByPrimaryKeySelectiveStatementId()));
        
        if(index >= 0)
        {
            AbstractXmlElementGenerator elementGenerator = new UpdateMultipleByPrimaryKeySelectiveElementGenerator(index + 1, introspectedTable.getPrimaryKeyColumns().size() > 1);
            
            elementGenerator.setContext(context);
            elementGenerator.setIntrospectedTable(introspectedTable);
            elementGenerator.addElements(document.getRootElement());
        }
        
        return true;
    }
}

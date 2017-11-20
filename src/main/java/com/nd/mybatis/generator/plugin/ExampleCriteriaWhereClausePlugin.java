/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.mybatis.generator.plugin;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import com.nd.mybatis.generator.dom.ExampleCriteraWhereClauseElementGenerator;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public class ExampleCriteriaWhereClausePlugin extends PluginAdapter
{
    private int index;
    
    private String clauseId;

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
    public void initialized(IntrospectedTable introspectedTable)
    {
        this.clauseId = properties.getProperty("clauseId", ExampleCriteraWhereClauseElementGenerator.CRITERIA_WHERE_CLAUSE_ID);
        
        try
        {
            this.index = Integer.valueOf(properties.getProperty("index"));
        }
        catch (Exception e)
        {
            this.index = ExampleCriteraWhereClauseElementGenerator.CRITERIA_WHERE_CLAUSE_INDEX;
        }
    }
    
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable)
    {
        AbstractXmlElementGenerator elementGenerator = new ExampleCriteraWhereClauseElementGenerator(clauseId, Math.min(document.getRootElement().getElements().size(), index));
        
        elementGenerator.setContext(context);
        elementGenerator.setIntrospectedTable(introspectedTable);
        elementGenerator.addElements(document.getRootElement());
        
        return true;
    }
}

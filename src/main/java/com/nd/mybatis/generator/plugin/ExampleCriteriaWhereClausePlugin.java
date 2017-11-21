/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.mybatis.generator.plugin;

import java.util.Iterator;
import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import com.nd.mybatis.generator.dom.ExampleCriteraWhereClauseElementGenerator;
import com.nd.mybatis.generator.util.JavaElementUtil;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public class ExampleCriteriaWhereClausePlugin extends PluginAdapter
{
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
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable)
    {
        AbstractXmlElementGenerator elementGenerator = null;
        
        int index = 0;
        int b = 0;
        
        for (Iterator<Element> iterator = document.getRootElement().getElements().iterator(); iterator.hasNext();)
        {
            Element element = iterator.next();

            if(JavaElementUtil.xmlElementMatcher(element, "sql", new Attribute("id", introspectedTable.getExampleWhereClauseId())))
            {
                //
                elementGenerator = new ExampleCriteraWhereClauseElementGenerator(clauseId, index);
                
                //
                document.getRootElement().getElements().set(index, generateExampleElement(false, introspectedTable));
                
                b += 1;
            }
            
            if(JavaElementUtil.xmlElementMatcher(element, "sql", new Attribute("id", introspectedTable.getMyBatis3UpdateByExampleWhereClauseId())))
            {
                document.getRootElement().getElements().set(index, generateExampleElement(true, introspectedTable));

                b += 1;
            }
            
            if(b >= 2)
            {
                break;
            }
            
            index++;
        }
        
        //
        if(elementGenerator != null)
        {
            elementGenerator.setContext(context);
            elementGenerator.setIntrospectedTable(introspectedTable);
            elementGenerator.addElements(document.getRootElement());
        }
        
        return true;
    }
    
    /**
     * {@link org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.ExampleWhereClauseElementGenerator#addElements(XmlElement)}
     *
     * @param isForUpdateByExample
     * @param introspectedTable
     * @return
     */
    private Element generateExampleElement(boolean isForUpdateByExample, IntrospectedTable introspectedTable)
    {
        XmlElement answer = new XmlElement("sql"); //$NON-NLS-1$

        if (isForUpdateByExample)
        {
            answer.addAttribute(new Attribute("id", introspectedTable.getMyBatis3UpdateByExampleWhereClauseId()));
        }
        else
        {
            answer.addAttribute(new Attribute("id", introspectedTable.getExampleWhereClauseId()));
        }

        context.getCommentGenerator().addComment(answer);

        XmlElement whereElement = new XmlElement("where");
        answer.addElement(whereElement);

        XmlElement outerForEachElement = new XmlElement("foreach");
        if (isForUpdateByExample)
        {
            outerForEachElement.addAttribute(new Attribute("collection", "example.oredCriteria"));
        }
        else
        {
            outerForEachElement.addAttribute(new Attribute("collection", "oredCriteria"));
        }
        
        outerForEachElement.addAttribute(new Attribute("item", "criteria"));
        outerForEachElement.addAttribute(new Attribute("separator", "or"));
        whereElement.addElement(outerForEachElement);
        
        XmlElement includeElement = new XmlElement("include");
        includeElement.addAttribute(new Attribute("refid", ExampleCriteraWhereClauseElementGenerator.CRITERIA_WHERE_CLAUSE_ID));
        
        outerForEachElement.addElement(includeElement);
        
        return answer;
    }
}

/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.mybatis.generator.dom;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.config.GeneratedKey;

import com.nd.mybatis.generator.plugin.InsertMultipleSelectivePlugin;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public class InsertMultipleSelectiveElementGenerator extends AbstractXmlElementGenerator
{

    public InsertMultipleSelectiveElementGenerator()
    {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement)
    {
        XmlElement answer = new XmlElement("insert");

        answer.addAttribute(new Attribute("id", InsertMultipleSelectivePlugin.ATTR_INSERT_MULTIPLE_SELECTIVE_STATEMENT_ID));

        answer.addAttribute(new Attribute("parameterType", "Map"));

        context.getCommentGenerator().addComment(answer);

        GeneratedKey gk = introspectedTable.getGeneratedKey();
        if (gk != null)
        {
            IntrospectedColumn introspectedColumn = introspectedTable.getColumn(gk.getColumn());
            // if the column is null, then it's a configuration error. The
            // warning has already been reported
            if (introspectedColumn != null)
            {
                if (gk.isJdbcStandard())
                {
                    answer.addAttribute(new Attribute("useGeneratedKeys", "true"));
                    answer.addAttribute(new Attribute("keyProperty", introspectedColumn.getJavaProperty()));
                }
                else
                {
                    answer.addElement(getSelectKey(introspectedColumn, gk));
                }
            }
        }

        StringBuilder sb = new StringBuilder();

        sb.append("insert into ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        XmlElement insertTrimElement = new XmlElement("trim");
        insertTrimElement.addAttribute(new Attribute("prefix", "("));
        insertTrimElement.addAttribute(new Attribute("suffix", ")"));
        insertTrimElement.addAttribute(new Attribute("suffixOverrides", ","));
        answer.addElement(insertTrimElement);
        
        XmlElement valuesTrimElement = new XmlElement("trim");
        valuesTrimElement.addAttribute(new Attribute("prefix", "("));
        valuesTrimElement.addAttribute(new Attribute("suffix", ")"));
        valuesTrimElement.addAttribute(new Attribute("suffixOverrides", ","));
        
        XmlElement foreachValuesElement = new XmlElement("foreach");
        foreachValuesElement.addAttribute(new Attribute("collection", "records"));
        foreachValuesElement.addAttribute(new Attribute("item", "item"));
        foreachValuesElement.addAttribute(new Attribute("index", "index"));
        foreachValuesElement.addAttribute(new Attribute("open", "values"));
        foreachValuesElement.addAttribute(new Attribute("separator", ","));
        
        foreachValuesElement.addElement(valuesTrimElement);
        
        answer.addElement(foreachValuesElement);

        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns())
        {
            if (introspectedColumn.isIdentity())
            {
                // cannot set values on identity fields
                continue;
            }

            if (introspectedColumn.isSequenceColumn() || introspectedColumn.getFullyQualifiedJavaType().isPrimitive())
            {
                // if it is a sequence column, it is not optional
                // This is required for MyBatis3 because MyBatis3 parses
                // and calculates the SQL before executing the selectKey

                // if it is primitive, we cannot do a null check
                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                sb.append(',');
                insertTrimElement.addElement(new TextElement(sb.toString()));

                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
                sb.append(',');
                valuesTrimElement.addElement(new TextElement(sb.toString()));

                continue;
            }

            XmlElement insertNotNullElement = new XmlElement("if");
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty("record."));
            sb.append(" != null");
            insertNotNullElement.addAttribute(new Attribute("test", sb.toString()));

            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(',');
            insertNotNullElement.addElement(new TextElement(sb.toString()));
            insertTrimElement.addElement(insertNotNullElement);

            XmlElement valuesNotNullElement = new XmlElement("if");
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty("record."));
            sb.append(" != null");
            valuesNotNullElement.addAttribute(new Attribute("test", sb.toString()));

            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "item."));
            sb.append(',');
            valuesNotNullElement.addElement(new TextElement(sb.toString()));
            valuesTrimElement.addElement(valuesNotNullElement);
        }

        if (context.getPlugins().sqlMapInsertSelectiveElementGenerated(answer, introspectedTable))
        {
            parentElement.addElement(answer);
        }
    }
}

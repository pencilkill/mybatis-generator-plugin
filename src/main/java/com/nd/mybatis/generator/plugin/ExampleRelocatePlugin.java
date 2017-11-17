/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.mybatis.generator.plugin;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public class ExampleRelocatePlugin extends PluginAdapter
{
    /**
     * @param warnings
     * @return
     * @see org.mybatis.generator.api.Plugin#validate(java.util.List)
     */
    @Override
    public boolean validate(List<String> warnings)
    {
        if (!StringUtility.stringHasValue(properties.getProperty("package")))
        {
            warnings.add("package must not be empty");

            return false;
        }

        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable)
    {
        introspectedTable.setExampleType(properties.getProperty("package") + "." + new FullyQualifiedJavaType(introspectedTable.getExampleType()).getShortName());
    }
}

/*
 * The MIT License
 *
 * Copyright (c) 2016, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.pipeline.config;

import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.Janitor;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.jenkinsci.plugins.pipeline.config.ast.ConfigASTPipelineDef;
import org.jenkinsci.plugins.pipeline.config.parser.Converter;
import org.jenkinsci.plugins.pipeline.config.parser.JSONParser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import static org.junit.Assert.fail;

public abstract class BaseParserLoaderTest extends AbstractConfigTest {
    public static String getSyntaxErrorReport(MultipleCompilationErrorsException e, String configName) {
        StringBuilder b = new StringBuilder();
        b.append("Config name: ");
        b.append(configName);
        b.append("\n");

        b.append("Errors:\n");
        for (Object o : e.getErrorCollector().getErrors()) {
            if (o instanceof SyntaxErrorMessage) {
                b.append(" - ");
                b.append(((SyntaxErrorMessage) o).getCause().getMessage());
                b.append("\n");
            }
        }

        return b.toString();

    }

    public static String getJSONErrorReport(JSONParser p, String configName) {
        StringBuilder b = new StringBuilder();
        b.append("Config name: ");
        b.append(configName);
        b.append("\n");

        b.append("Errors:\n");
        for (String e : p.getErrorCollector().errorsAsStrings()) {
            b.append(" - ");
            b.append(e);
            b.append("\n");
        }

        return b.toString();
    }

    /**
     * Parses the given Groovy source file into a model.
     */
    public ConfigASTPipelineDef parse(URL src) throws Exception {
        return Converter.urlToPipelineDef(src);
    }

    /**
     * Parses the given Groovy source string into a model.
     */
    public ConfigASTPipelineDef parse(String src) throws Exception {
        return Converter.scriptToPipelineDef(src);
    }

    /**
     * Parses the source code and report errors.
     */
    protected ErrorCollector parseForError(URL src) throws Exception {
        try {
            parse(src);
            fail("Expected compilation to fail");
            throw new AssertionError();
        } catch (MultipleCompilationErrorsException e) {
            return e.getErrorCollector();
        }
    }

    /**
     * Prints errors into a string.
     */
    protected String write(ErrorCollector ec) {
        Janitor janitor = new Janitor();
        try {
            StringWriter data = new StringWriter();
            PrintWriter writer = new PrintWriter(data);
            ec.write(writer, janitor);
            return data.toString();
        } finally {
            janitor.cleanup();
        }
    }

}
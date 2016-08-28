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
package org.jenkinsci.plugins.pipeline.config.parser;

import net.sf.json.JSONObject;
import org.jenkinsci.plugins.pipeline.config.AbstractConfigTest;
import org.jenkinsci.plugins.pipeline.config.BaseParserLoaderTest;
import org.jenkinsci.plugins.pipeline.config.ast.ConfigASTPipelineDef;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class ConvertRoundTripTest extends BaseParserLoaderTest {

    private String configName;

    public ConvertRoundTripTest(String configName) {
        this.configName = configName;
    }

    @Parameterized.Parameters(name = "Name: {0}")
    public static Iterable<Object[]> generateParameters() {
        List<Object[]> result = new ArrayList<>();
        for (String c : AbstractConfigTest.SHOULD_PASS_CONFIGS) {
            result.add(new Object[]{c});
        }

        return result;
    }

    @Test
    public void groovyToASTToJSONToAST() throws Exception {
        ConfigASTPipelineDef origRoot = Converter.urlToPipelineDef(getClass().getResource("/" + configName + ".groovy"));

        assertNotNull(origRoot);

        JSONObject origJson = origRoot.toJSON();
        assertNotNull(origJson);

        JSONParser jp = new JSONParser(origJson);
        ConfigASTPipelineDef newRoot = jp.parse();

        assertEquals(getJSONErrorReport(jp, configName), 0, jp.getErrorCollector().getErrorCount());
        assertNotNull("Pipeline null for " + configName, newRoot);

        assertEquals(origRoot, newRoot);
    }

    @Test
    public void groovyToASTToGroovyToAST() throws Exception {
        ConfigASTPipelineDef origRoot = Converter.urlToPipelineDef(getClass().getResource("/" + configName + ".groovy"));

        assertNotNull(origRoot);

        String prettyGroovy = origRoot.toPrettyGroovy();
        assertNotNull(prettyGroovy);

        ConfigASTPipelineDef newRoot = parse(prettyGroovy);
        assertNotNull(newRoot);

        assertEquals(origRoot, newRoot);
    }

    @Test
    public void jsonToASTToJSON() throws Exception {
        JSONObject origJson = JSONObject.fromObject(fileContentsFromResources("json/" + configName + ".json"));
        assertNotNull("Couldn't parse JSON for " + configName, origJson);

        JSONParser jp = new JSONParser(origJson);
        ConfigASTPipelineDef pipelineDef = jp.parse();

        assertEquals(getJSONErrorReport(jp, configName), 0, jp.getErrorCollector().getErrorCount());
        assertNotNull("Pipeline null for " + configName, pipelineDef);

        assertEquals(origJson, pipelineDef.toJSON());
    }

    @Test
    public void jsonToASTToGroovyToAST() throws Exception {
        JSONObject origJson = JSONObject.fromObject(fileContentsFromResources("json/" + configName + ".json"));
        assertNotNull("Couldn't parse JSON for " + configName, origJson);

        JSONParser jp = new JSONParser(origJson);
        ConfigASTPipelineDef origRoot = jp.parse();

        assertEquals(getJSONErrorReport(jp, configName), 0, jp.getErrorCollector().getErrorCount());
        assertNotNull("Pipeline null for " + configName, origRoot);

        String prettyGroovy = origRoot.toPrettyGroovy();
        assertNotNull(prettyGroovy);

        ConfigASTPipelineDef newRoot = parse(prettyGroovy);
        assertNotNull(newRoot);

        assertEquals(origRoot, newRoot);
    }
}
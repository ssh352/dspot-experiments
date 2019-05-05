package com.github.mustachejava;


import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public final class AmplNestedPartialTest {
    private static final String TEMPLATE_FILE = "nested_partials_template.html";

    private static File root;

    @BeforeClass
    public static void setUp() throws Exception {
        File file = new File("compiler/src/test/resources");
        AmplNestedPartialTest.root = (new File(file, AmplNestedPartialTest.TEMPLATE_FILE).exists()) ? file : new File("src/test/resources");
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add14null705_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add14null705 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15null714_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            });
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null714 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add14null704_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add14null704 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString6null700_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("2gjtvlDf", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString6null700 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add631_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                Arrays.asList("w00pw00p", "mustache rocks");
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add631 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString3null702_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("300pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString3null702 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null701_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "page1.txt");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null701 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add630_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                });
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add630 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString4null692_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pwg00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString4null692 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add16null708_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            List<String> o_should_handle_more_than_one_level_of_partial_nesting_add16__7 = Arrays.asList("w00pw00p", "mustache rocks");
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add16null708 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString355_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w?0pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString355 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString354_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00w00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString354 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString353_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("page1.txt", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString353 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_remove19null715_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_remove19null715 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString5null698_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw0p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString5null698 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null696_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null696 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString8null695_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache 9rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString8null695 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString10null699_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "QJ=Z!^`s,dmm(l");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString10null699 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString2null697_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("page1.txt", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString2null697 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString359_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mstache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString359 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString11null703_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustach rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString11null703 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString12null693_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache roqks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString12null693 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add13null711_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache o_should_handle_more_than_one_level_of_partial_nesting_add13__3 = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add13null711 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add632_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add632 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add633_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add633 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15null713_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            });
            maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null713 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add17null712_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add17null712 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }
}

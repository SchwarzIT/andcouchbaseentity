package com.kaufland;


import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;

public class CoachBaseBinderProcessorTest {

    @Test
    public void testSuccessProcessing() {

        Compilation compilation =
                javac()
                        .withProcessors(new CoachBaseBinderProcessor())
                        .compile(JavaFileObjects.forSourceString("com.kaufland.testModels.ListTest", "package com.kaufland.testModels;\n" +
                                "\n" +
                                "import java.util.ArrayList;\n" +
                                "import kaufland.com.coachbasebinderapi.CblEntity;\n" +
                                "import kaufland.com.coachbasebinderapi.CblField;\n" +
                                "import java.io.InputStream;\n" +
                                "\n" +
                                "@CblEntity\n" +
                                "public class ListTest {\n" +
                                "\n" +
                                "\n" +
                                "    @CblField\n" +
                                "    private String type;\n" +
                                "\n" +
                                "    @CblField(\"title\")\n" +
                                "    private String title;\n" +
                                "\n" +
                                "    @CblField(\"created_at\")\n" +
                                "    private String createdAt;\n" +
                                "\n" +
                                "    @CblField(\"members\")\n" +
                                "    private ArrayList<String> members;\n" +
                                "\n" +
                                "    @CblField(\"owner\")\n" +
                                "    private String owner;\n" +
                                "    @CblField(value = \"image\", attachmentType = \"image/jpg\")\n" +
                                "    private InputStream image;" +
                                "\n" +
                                "\n" +
                                "}"));

        Assert.assertEquals(compilation.status(), Compilation.Status.SUCCESS);
    }

    @Test
    public void testSuccessSubEntityProcessing() {

        JavaFileObject mMainEntity = JavaFileObjects.forSourceString("com.kaufland.testModels.ListTest", "package com.kaufland.testModels;\n" +
                "\n" +
                "import java.util.ArrayList;\n" +
                "import kaufland.com.coachbasebinderapi.CblEntity;\n" +
                "import kaufland.com.coachbasebinderapi.CblField;\n" +
                "import java.io.InputStream;\n" +
                "\n" +
                "@CblEntity\n" +
                "public class ListTest {\n" +
                "\n" +
                "    @CblField(\"list_sub\")\n" +
                "    private ArrayList<Sub> listSub;\n" +
                "    @CblField(\"sub\")\n" +
                "    private Sub sub;" +
                "}");
        JavaFileObject subEntity = JavaFileObjects.forSourceString("com.kaufland.testModels.Sub", "package com.kaufland.testModels;\n" +
                "\n" +
                "import kaufland.com.coachbasebinderapi.CblEntity;\n" +
                "import kaufland.com.coachbasebinderapi.CblField;\n" +
                "\n" +
                "/**\n" +
                " * Created by sbra0902 on 31.05.17.\n" +
                " */\n" +
                "@CblEntity\n" +
                "public class Sub {\n" +
                "\n" +
                "    @CblField\n" +
                "    private String test;\n" +
                "\n" +
                "}");
        Compilation compilation =
                javac()
                        .withProcessors(new CoachBaseBinderProcessor())
                        .compile(mMainEntity, subEntity);

        Assert.assertEquals(compilation.status(), Compilation.Status.SUCCESS);
    }

    @Test
    public void testFailContructorAndPublicFieldProcessing() {

        JavaFileObject subEntity = JavaFileObjects.forSourceString("com.kaufland.testModels.Sub", "package com.kaufland.testModels;\n" +
                "\n" +
                "import kaufland.com.coachbasebinderapi.CblEntity;\n" +
                "import kaufland.com.coachbasebinderapi.CblField;\n" +
                "\n" +
                "/**\n" +
                " * Created by sbra0902 on 31.05.17.\n" +
                " */\n" +
                "@CblEntity\n" +
                "public class Sub {\n" +
                "\n" +
                " public Sub(String test){\n" +
                " }\n" +
                "\n" +
                "    @CblField\n" +
                "    public String test;\n" +
                "\n" +
                "}");


        Compilation compilation =
                javac()
                        .withProcessors(new CoachBaseBinderProcessor())
                        .compile(subEntity);


        Assert.assertEquals(compilation.status(), Compilation.Status.FAILURE);
        Assert.assertEquals(compilation.diagnostics().size(), 2);
        Assert.assertEquals(compilation.diagnostics().get(0).getMessage(Locale.GERMAN), "CblEntity should not have a contructor");
        Assert.assertEquals(compilation.diagnostics().get(1).getMessage(Locale.GERMAN), "CblField must be private");
    }
}
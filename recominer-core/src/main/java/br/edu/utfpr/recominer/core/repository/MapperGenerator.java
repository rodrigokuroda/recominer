package br.edu.utfpr.recominer.core.repository;

import com.google.common.base.CaseFormat;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.springframework.util.StringUtils;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class MapperGenerator {

    private static final Class<?> clazz = MapperGenerator.class;

    public static void main(String[] args) {

        StringBuilder mapper = new StringBuilder();
        final String variableName = StringUtils.uncapitalize(clazz.getSimpleName());

        mapper.append("package br.edu.utfpr.recominer.repository;\n"
                + "\n"
                + "import br.edu.utfpr.recominer.model." + clazz.getSimpleName() + ";\n"
                + "import br.edu.utfpr.recominer.core.model.Project;\n"
                + "import br.edu.utfpr.recominer.core.repository.helper.RowUnmapper;\n"
                + "import br.edu.utfpr.recominer.core.repository.helper.TableDescription;\n"
                + "import java.sql.ResultSet;\n"
                + "import java.util.LinkedHashMap;\n"
                + "import java.util.List;\n"
                + "import java.util.Map;\n"
                + "import org.springframework.jdbc.core.RowMapper;\n"
                + "import org.springframework.stereotype.Repository;\n"
                + "\n"
                + "/**\n"
                + " *\n"
                + " * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>\n"
                + " */\n"
                + "@Repository\n"
                + "public class " + clazz.getSimpleName() + "Repository extends JdbcRepository<" + clazz.getSimpleName() + ", Integer> {\n"
                + "\n"
                + "    public " + clazz.getSimpleName() + "Repository() {\n"
                + "        super(ROW_MAPPER, ROW_UNMAPPER, TABLE_NAME, ID_COLUMN);\n"
                + "    }\n"
                + "    \n"
                + "    public void setProject(Project project) {\n"
                + "        setTable(new TableDescription(project.getProjectName(), TABLE_NAME, ID_COLUMN));\n"
                + "    }\n"
                + "    \n"
                + "    private static final String ID_COLUMN = \"id\";\n"
                + "    private static final String TABLE_NAME = \"" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName()) + "\";\n"
                + "\n");

        mapper.append("    public static final RowMapper<" + clazz.getSimpleName() + "> ROW_MAPPER\n"
                + "            = (ResultSet rs, int rowNum) -> {\n"
                + "                " + clazz.getSimpleName() + " " + variableName + " = new " + clazz.getSimpleName() + "();\n");
        for (Method method : clazz.getMethods()) {
            if (method.getName().startsWith("set")) {
                String attribute = method.getName().replace("set", "");
                final Parameter parameter = method.getParameters()[0];
                final String paramType = parameter.getType().getSimpleName();
                final String paramFullType = parameter.getType().getName();

                mapper.append("                ").append(variableName).append(".").append(method.getName());

                String rsGet = null;
                if (paramType.equals("Integer")) {
                    rsGet = "Int";
                } else if (paramFullType.startsWith("java")
                        || parameter.getType().isPrimitive()) {
                    rsGet = StringUtils.capitalize(paramType);
                }

                if (rsGet == null) {
                    mapper.append(
                            "(new " + paramType + "(rs.getInt(\""
                            + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, attribute) + "\")));\n"
                    );
                } else {
                    mapper.append(
                            "(rs.get" + rsGet + "(\""
                            + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, attribute) + "\"));\n"
                    );

                }
            }
        }
        mapper.append("                return " + variableName + ";\n");

        StringBuilder unmapper = new StringBuilder();
        unmapper.append("\n    public static final RowUnmapper<" + clazz.getSimpleName() + "> ROW_UNMAPPER\n"
                + "            = (" + clazz.getSimpleName() + " " + variableName + ") -> {\n"
                + "                Map<String, Object> mapping = new LinkedHashMap<>();\n");
        for (Method method : clazz.getMethods()) {
            if (method.getName().startsWith("set")) {
                String attribute = method.getName().replace("set", "");
                final Parameter parameter = method.getParameters()[0];
                final String paramType = parameter.getType().getSimpleName();
                final String paramFullType = parameter.getType().getName();

                unmapper.append("                mapping.put(\"" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, attribute)
                        + "\", " + variableName + "." + method.getName().replace("set", "get") + "()");
                String rsGet = null;
                if (paramFullType.startsWith("java")
                        || parameter.getType().isPrimitive()) {
                    rsGet = paramType;
                }

                if (rsGet == null) {
                    unmapper.append(".getId());\n");
                } else {
                    unmapper.append(");\n");
                }
            }
        }
        unmapper.append("                return mapping;\n"
                + "            };\n");

        mapper.append("            };\n").append(unmapper)
                .append("\n}");

        System.out.println(mapper.toString());
    }
}

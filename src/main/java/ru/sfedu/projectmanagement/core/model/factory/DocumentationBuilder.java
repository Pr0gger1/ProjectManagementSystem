package ru.sfedu.projectmanagement.core.model.factory;

import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.model.Documentation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentationBuilder extends EntityBuilder {
    private final HashMap<String, String> body = new HashMap<>();

    @Override
    public Documentation build(String[] args) throws IllegalArgumentException {
        if (args.length != Constants.DOCUMENTATION_PRIMITIVE_PARAMETER_COUNT)
            throw new IllegalArgumentException(Constants.INVALID_PARAMETERS_MESSAGE);

        Documentation documentation = new Documentation();

        documentation.setName(args[0]);
        documentation.setDescription(args[1]);
        documentation.setProjectId(UUID.fromString(args[2]));
        documentation.setEmployeeId(UUID.fromString(args[3]));
        documentation.setEmployeeFullName(args[4]);
        documentation.setBody(body);

        return documentation;
    }

    public DocumentationBuilder parseDocBody(String[] args) {
        String regex = "\\{([^{}]+),([^{}]+)}";
        Pattern pattern = Pattern.compile(regex);

        Arrays.stream(args).forEach(
                entry -> {
                    Matcher matcher = pattern.matcher(entry);
                    if (matcher.matches()) {
                        body.put(matcher.group(1), matcher.group(2));
                    }
                }
        );
        return this;
    }
}

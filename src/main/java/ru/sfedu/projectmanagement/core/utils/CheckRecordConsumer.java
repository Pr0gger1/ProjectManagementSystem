package ru.sfedu.projectmanagement.core.utils;

import java.util.UUID;

public interface CheckRecordConsumer {
    boolean accept(String filePath, UUID id);
}

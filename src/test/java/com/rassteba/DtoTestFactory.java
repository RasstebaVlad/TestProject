package com.rassteba;

import com.rassteba.test.DocumentManager;
import net.bytebuddy.utility.RandomString;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class DtoTestFactory {

  public static DocumentManager.Document buildDocument() {
    return DocumentManager.Document.builder()
        .id(UUID.randomUUID().toString())
        .title(RandomString.make(10))
        .content(RandomString.make(50))
        .author(buildAuthor())
        .created(Instant.now())
        .build();
  }

  public static DocumentManager.Author buildAuthor() {
    return DocumentManager.Author.builder()
        .id(UUID.randomUUID().toString())
        .name(RandomString.make(10))
        .build();
  }

  public static DocumentManager.SearchRequest buildSearchRequest() {
    return DocumentManager.SearchRequest.builder()
        .titlePrefixes(List.of(RandomString.make(3), RandomString.make(3)))
        .containsContents(List.of(RandomString.make(5), RandomString.make(5)))
        .authorIds(List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()))
        .createdFrom(Instant.now())
        .createdTo(Instant.now())
        .build();
  }
}

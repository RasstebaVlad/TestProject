package com.rassteba.test;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
@Getter
public class DocumentManager {

  final Map<String, Document> storage = new ConcurrentHashMap<>();

  /**
   * Implementation of this method should upsert the document to your storage
   * And generate unique id if it does not exist, don't change [created] field
   *
   * @param document - document content and author data
   * @return saved document
   */
  public Document save(Document document) {
    if (Objects.isNull(document.getId()) || !storage.containsKey(document.getId())) {
      document = Document.builder()
          .id(UUID.randomUUID().toString())
          .title(document.getTitle())
          .content(document.getContent())
          .author(document.getAuthor())
          .created(Instant.now())
          .build();
    }
    storage.put(document.getId(), document);
    return document;
  }

  /**
   * Implementation this method should find documents which match with request
   *
   * @param request - search request, each field could be null
   * @return list matched documents
   */
  public List<Document> search(SearchRequest request) {
    return storage.values().stream()
        .filter(doc -> Objects.isNull(request.getTitlePrefixes()) || request.getTitlePrefixes().stream().anyMatch(prefix -> doc.getTitle().startsWith(prefix)))
        .filter(doc -> Objects.isNull(request.getContainsContents()) || request.getContainsContents().stream().anyMatch(content -> doc.getContent().contains(content)))
        .filter(doc -> Objects.isNull(request.getAuthorIds()) || request.getAuthorIds().contains(doc.getAuthor().getId()))
        .filter(doc -> (Objects.isNull(request.getCreatedFrom()) || !doc.getCreated().isBefore(request.getCreatedFrom())) &&
            (Objects.isNull(request.getCreatedTo()) || !doc.getCreated().isAfter(request.getCreatedTo())))
        .toList();
  }

  /**
   * Implementation this method should find document by id
   *
   * @param id - document id
   * @return optional document
   */
  public Optional<Document> findById(String id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Data
  @Builder
  public static class SearchRequest {
    private List<String> titlePrefixes;
    private List<String> containsContents;
    private List<String> authorIds;
    private Instant createdFrom;
    private Instant createdTo;
  }

  @Data
  @Builder
  public static class Document {
    private String id;
    private String title;
    private String content;
    private Author author;
    private Instant created;
  }

  @Data
  @Builder
  public static class Author {
    private String id;
    private String name;
  }
}
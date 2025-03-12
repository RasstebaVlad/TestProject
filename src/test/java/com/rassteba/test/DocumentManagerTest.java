package com.rassteba.test;

import com.rassteba.DtoTestFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ExtendWith(MockitoExtension.class)
public class DocumentManagerTest {

  @Mock
  private Map<String, DocumentManager.Document> storage;
  @InjectMocks
  private DocumentManager documentManager;

  @BeforeEach
  void setUp() {
    DocumentManager.Document documentOne = DocumentManager.Document.builder()
        .id("1")
        .title("1Title")
        .content("1Content cur")
        .author(new DocumentManager.Author("1", "Taras"))
        .created(Instant.now())
        .build();
    DocumentManager.Document documentTwo = DocumentManager.Document.builder()
        .title("2Title")
        .content("2Content map")
        .author(new DocumentManager.Author("2", "Vladyslav"))
        .created(Instant.now())
        .build();
    DocumentManager.Document documentThree =DocumentManager.Document.builder()
        .id("2")
        .title("3Title")
        .content("3Content bug")
        .author(new DocumentManager.Author("3", "Andrey"))
        .created(Instant.now())
        .build();

    DocumentManager.Document savedDocumentOne = documentManager.save(documentOne);
    DocumentManager.Document savedDocumentTwo = documentManager.save(documentTwo);
    DocumentManager.Document savedDocumentThree = documentManager.save(documentThree);

    this.storage = new ConcurrentHashMap<>();
    storage.put(savedDocumentOne.getId(), savedDocumentOne);
    storage.put(savedDocumentTwo.getId(), savedDocumentTwo);
    storage.put(savedDocumentThree.getId(), savedDocumentThree);
  }

  @Test
  public void test_save_WhenDocumentIdIsNull() {
    DocumentManager.Document document = DtoTestFactory.buildDocument();
    document.setId(null);

    DocumentManager.Document savedDocument = documentManager.save(document);

    Assertions.assertNotNull(savedDocument);
    Assertions.assertNotNull(savedDocument.getId());
    Assertions.assertEquals(document.getTitle(), savedDocument.getTitle());
    Assertions.assertEquals(document.getContent(), savedDocument.getContent());
    Assertions.assertEquals(document.getAuthor(), savedDocument.getAuthor());
  }

  @Test
  public void test_save_WhenDocumentIdNotExistInStorage() {
    DocumentManager.Document document = DtoTestFactory.buildDocument();

    DocumentManager.Document savedDocument = documentManager.save(document);

    Assertions.assertNotNull(savedDocument);
    Assertions.assertNotNull(savedDocument.getId());
    Assertions.assertEquals(document.getTitle(), savedDocument.getTitle());
    Assertions.assertEquals(document.getContent(), savedDocument.getContent());
    Assertions.assertEquals(document.getAuthor(), savedDocument.getAuthor());
  }

  @Test
  public void test_save_WhenDocumentIdExistInStorage() {
    DocumentManager.Document document = DtoTestFactory.buildDocument();
    document.setId(storage.entrySet().stream().findFirst().get().getKey());
    DocumentManager.Document savedDocument = documentManager.save(document);

    Assertions.assertNotNull(savedDocument);
    Assertions.assertEquals(document.getId(), savedDocument.getId());
    Assertions.assertEquals(document.getTitle(), savedDocument.getTitle());
    Assertions.assertEquals(document.getContent(), savedDocument.getContent());
    Assertions.assertEquals(document.getAuthor(), savedDocument.getAuthor());
  }

  @Test
  public void test_search_WhenNoFilters() {
    DocumentManager.SearchRequest searchRequest = DtoTestFactory.buildSearchRequest();
    searchRequest.setTitlePrefixes(null);
    searchRequest.setContainsContents(null);
    searchRequest.setAuthorIds(null);
    searchRequest.setCreatedFrom(null);
    searchRequest.setCreatedTo(null);

    List<DocumentManager.Document> searchedDocuments = documentManager.search(searchRequest);

    Assertions.assertEquals(3, searchedDocuments.size());
  }

  @Test
  public void test_search_FilterByTitlePrefix() {
    DocumentManager.SearchRequest searchRequest = DtoTestFactory.buildSearchRequest();
    searchRequest.setTitlePrefixes(List.of("1T",  "2T"));
    searchRequest.setContainsContents(null);
    searchRequest.setAuthorIds(null);
    searchRequest.setCreatedFrom(null);
    searchRequest.setCreatedTo(null);

    List<DocumentManager.Document> searchedDocuments = documentManager.search(searchRequest);

    Assertions.assertEquals(2, searchedDocuments.size());
  }

  @Test
  public void test_search_FilterByContent() {
    DocumentManager.SearchRequest searchRequest = DtoTestFactory.buildSearchRequest();
    searchRequest.setTitlePrefixes(null);
    searchRequest.setContainsContents(List.of("1C", "bug"));
    searchRequest.setAuthorIds(null);
    searchRequest.setCreatedFrom(null);
    searchRequest.setCreatedTo(null);

    List<DocumentManager.Document> searchedDocuments = documentManager.search(searchRequest);

    Assertions.assertEquals(2, searchedDocuments.size());
  }

  @Test
  public void test_search_FilterByAuthorId() {
    DocumentManager.SearchRequest searchRequest = DtoTestFactory.buildSearchRequest();
    searchRequest.setTitlePrefixes(null);
    searchRequest.setContainsContents(null);
    searchRequest.setAuthorIds(List.of("2"));
    searchRequest.setCreatedFrom(null);
    searchRequest.setCreatedTo(null);

    List<DocumentManager.Document> searchedDocuments = documentManager.search(searchRequest);

    Assertions.assertEquals(1, searchedDocuments.size());
  }

  @Test
  public void test_search_FilterByDateRange() {
    DocumentManager.SearchRequest searchRequest = DtoTestFactory.buildSearchRequest();
    searchRequest.setTitlePrefixes(null);
    searchRequest.setContainsContents(null);
    searchRequest.setAuthorIds(null);
    searchRequest.setCreatedFrom(Instant.now().minusSeconds(50));
    searchRequest.setCreatedTo(Instant.now().plusSeconds(50));

    List<DocumentManager.Document> searchedDocuments = documentManager.search(searchRequest);

    Assertions.assertEquals(3, searchedDocuments.size());
  }
  @Test
  public void test_search_AllFilters() {
    DocumentManager.SearchRequest searchRequest = DtoTestFactory.buildSearchRequest();
    searchRequest.setTitlePrefixes(List.of("1T"));
    searchRequest.setContainsContents(List.of("1C"));
    searchRequest.setAuthorIds(List.of("1"));
    searchRequest.setCreatedFrom(Instant.now().minusSeconds(50));
    searchRequest.setCreatedTo(Instant.now().plusSeconds(50));

    List<DocumentManager.Document> searchedDocuments = documentManager.search(searchRequest);

    Assertions.assertEquals(1, searchedDocuments.size());
  }

  @Test
  public void test_findById() {
    Optional<DocumentManager.Document> document = documentManager.findById(storage.entrySet().stream().findFirst().get().getKey());
    Assertions.assertTrue(document.isPresent());
  }
}

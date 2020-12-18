package com.lambdaschool.bookstore.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaschool.bookstore.BookstoreApplication;
import com.lambdaschool.bookstore.exceptions.ResourceNotFoundException;
import com.lambdaschool.bookstore.models.Author;
import com.lambdaschool.bookstore.models.Book;
import com.lambdaschool.bookstore.models.Section;
import com.lambdaschool.bookstore.models.Wrote;
import com.lambdaschool.bookstore.services.BookService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)

/*****
 * Due to security being in place, we have to switch out WebMvcTest for SpringBootTest
 * @WebMvcTest(value = BookController.class)
 */
@SpringBootTest(classes = BookstoreApplication.class)

/****
 * This is the user and roles we will use to test!
 */
@WithMockUser(username = "admin", roles = {"ADMIN", "DATA"})
public class BookControllerTest
{
    /******
     * WebApplicationContext is needed due to security being in place.
     */
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    List<Book> bookList = new ArrayList<>();

    @Before
    public void setUp() throws
            Exception
    {
        /*****
         * The following is needed due to security being in place!
         */
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        /*****
         * Note that since we are only testing bookstore data, you only need to mock up bookstore data.
         * You do NOT need to mock up user data. You can. It is not wrong, just extra work.
         */
        Author a1 = new Author("John", "Mitchell");
        Author a2 = new Author("Dan", "Brown");
        Author a3 = new Author("Jerry", "Poe");
        Author a4 = new Author("Wells", "Teague");
        Author a5 = new Author("George", "Gallinger");
        Author a6 = new Author("Ian", "Stewart");

        Section s1 = new Section("Fiction");
        Section s2 = new Section("Technology");
        Section s3 = new Section("Travel");
        Section s4 = new Section("Business");
        Section s5 = new Section("Religion");

        Set<Wrote> wrote = new HashSet<>();
        wrote.add(new Wrote(a6, new Book()));
        Book b1 = new Book("Flatterland", "9780738206752", 2001, s1);
        b1.setWrotes(wrote);
        b1.setBookid(1);
        bookList.add(b1);

        wrote = new HashSet<>();
        wrote.add(new Wrote(a2, new Book()));
        Book b2 = new Book("Digital Fortess", "9788489367012", 2007, s1);
        b2.setWrotes(wrote);
        b2.setBookid(2);
        bookList.add(b2);

        wrote = new HashSet<>();
        wrote.add(new Wrote(a2, new Book()));
        Book b3 = new Book("The Da Vinci Code", "9780307474278", 2009, s1);
        b3.setWrotes(wrote);
        b3.setBookid(3);
        bookList.add(b3);

        wrote = new HashSet<>();
        wrote.add(new Wrote(a5, new Book()));
        wrote.add(new Wrote(a3, new Book()));
        Book b4 = new Book("Essentials of Finance", "1314241651234", 0, s4);
        b4.setWrotes(wrote);
        b4.setBookid(4);
        bookList.add(b4);

        wrote = new HashSet<>();
        wrote.add(new Wrote(a4, new Book()));
        Book b5 = new Book("Calling Texas Home", "1885171382134", 2000, s3);
        b5.setWrotes(wrote);
        b5.setBookid(5);
        bookList.add(b5);
    }

    @After
    public void tearDown() throws
            Exception
    {
    }

    @Test
    public void listAllBooks() throws
            Exception
    {
        String uri = "/books/books";
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = result.getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    public void getBookById() throws
            Exception
    {
        String uri = "/books/book/{bookid}";

        String bookAsJson = objectMapper.writeValueAsString(bookList.get(1));

        Mockito.when(bookService.findBookById(1L)).thenReturn(bookList.get(1));
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(uri, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookAsJson);

        mockMvc.perform(mockRequest).andExpect(status().isOk());
    }

    @Test
    public void getNoBookById() throws
            Exception
    {
        String uri = "/books/book/100";

        Mockito.when(bookService.findBookById(100)).thenReturn(null);
        RequestBuilder mockRequest = MockMvcRequestBuilders
                .get(uri)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(mockRequest).andReturn();
        String content = result.getResponse().getContentAsString();
        String empty = "";

        assertEquals(content, empty);
    }

    @Test
    public void addNewBook() throws
            Exception
    {
        String apiUrl = "/books/book";
        String newBookName = "Seth's Life: An Autobiography";
        Book newBook = new Book(
               newBookName,
                "1885171382135",
                2000,
                bookList.get(0).getSection()
        );

        ObjectMapper mapper = new ObjectMapper();
        String bookString = mapper.writeValueAsString(newBook);

        Mockito.when(bookService.save(any(Book.class)))
                .thenReturn(newBook);

        RequestBuilder rb = MockMvcRequestBuilders.post(apiUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookString);

        mockMvc.perform(rb)
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateFullBook() throws Exception
    {
        String apiUrl = "/books/book/{bookid}";
        String newBookName = "Seth's Life: An Autobiography";
        Book newBook = new Book(
                newBookName,
                "1885171382135",
                2000,
                bookList.get(0).getSection()
        );
        newBook.setBookid(4);

        Mockito.when(bookService.update(newBook,4L))
                .thenReturn(newBook);

        ObjectMapper mapper = new ObjectMapper();
        String bookString = mapper.writeValueAsString(newBook);

        RequestBuilder rb = MockMvcRequestBuilders.put(apiUrl, 4L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(bookString);

        mockMvc.perform(rb)
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void deleteBookById() throws
            Exception
    {
        String apiUrl = "/books/book/{id}";

        RequestBuilder rb = MockMvcRequestBuilders.delete(apiUrl, "4")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(rb)
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
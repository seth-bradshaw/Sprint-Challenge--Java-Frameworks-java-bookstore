package com.lambdaschool.bookstore.services;

import com.lambdaschool.bookstore.BookstoreApplication;
import com.lambdaschool.bookstore.exceptions.ResourceNotFoundException;
import com.lambdaschool.bookstore.models.Author;
import com.lambdaschool.bookstore.models.Book;
import com.lambdaschool.bookstore.models.Section;
import com.lambdaschool.bookstore.models.Wrote;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BookstoreApplication.class)
//**********
// Note security is handled at the controller, hence we do not need to worry about security here!
//**********
public class BookServiceImplTest
{

    @Autowired
    private BookService bookService;

    @Autowired
    private SectionService sectionService;

    @Before
    public void setUp() throws
            Exception
    {
//        Author a1 = new Author("John", "Mitchell");
//        Author a2 = new Author("Dan", "Brown");
//        Author a3 = new Author("Jerry", "Poe");
//        Author a4 = new Author("Wells", "Teague");
//        Author a5 = new Author("George", "Gallinger");
//        Author a6 = new Author("Ian", "Stewart");
//
//        a1 = authorService.save(a1);
//        a2 = authorService.save(a2);
//        a3 = authorService.save(a3);
//        a4 = authorService.save(a4);
//        a5 = authorService.save(a5);
//        a6 = authorService.save(a6);
//
//        Section s1 = new Section("Fiction");
//        Section s2 = new Section("Technology");
//        Section s3 = new Section("Travel");
//        Section s4 = new Section("Business");
//        Section s5 = new Section("Religion");
//
//        s1 = sectionService.save(s1);
//        s2 = sectionService.save(s2);
//        s3 = sectionService.save(s3);
//        s4 = sectionService.save(s4);
//        s5 = sectionService.save(s5);
//
//        Set<Wrote> wrote = new HashSet<>();
//        wrote.add(new Wrote(a6, new Book()));
//        Book b1 = new Book("Flatterland", "9780738206752", 2001, s1);
//        b1.setWrotes(wrote);
//        b1 = bookService.save(b1);
//
//        wrote = new HashSet<>();
//        wrote.add(new Wrote(a2, new Book()));
//        Book b2 = new Book("Digital Fortess", "9788489367012", 2007, s1);
//        b2.setWrotes(wrote);
//        b2 = bookService.save(b2);
//
//        wrote = new HashSet<>();
//        wrote.add(new Wrote(a2, new Book()));
//        Book b3 = new Book("The Da Vinci Code", "9780307474278", 2009, s1);
//        b3.setWrotes(wrote);
//        b3 = bookService.save(b3);
//
//        wrote = new HashSet<>();
//        wrote.add(new Wrote(a5, new Book()));
//        wrote.add(new Wrote(a3, new Book()));
//        Book b4 = new Book("Essentials of Finance", "1314241651234", 0, s4);
//        b4.setWrotes(wrote);
//        b4 = bookService.save(b4);
//
//        wrote = new HashSet<>();
//        wrote.add(new Wrote(a4, new Book()));
//        Book b5 = new Book("Calling Texas Home", "1885171382134", 2000, s3);
//        b5.setWrotes(wrote);
//        b5 = bookService.save(b5);
//
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws
            Exception
    {
    }

    @Test
    public void findAll()
    {
        assertEquals(4, bookService.findAll().size());
    }

    @Test
    public void findBookById()
    {
        System.out.println(bookService.findBookById(30));
        assertEquals("Calling Texas Home", bookService.findBookById(30).getTitle());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void notFindBookById()
    {
        assertEquals("Calling Texas Home", bookService.findBookById(1000).getTitle());
    }

    @Test
    public void delete()
    {
        bookService.delete(30);
        assertEquals(4L, bookService.findAll().size());
    }

    @Test
    public void save()
    {
        String newBookName = "Seth's Life: An Autobiography";
        Book newBook = new Book();
        newBook.setTitle(newBookName);
        newBook.setIsbn("1885171382135");
        newBook.setCopy(2000);
        newBook.setSection(sectionService.findSectionById(22));

        Book addBook = bookService.save(newBook);
        assertNotNull(addBook);
        assertEquals(newBookName, addBook.getTitle());
    }

    @Test
    public void update()
    {
        String newBookName = "Seth's Life: An Autobiography";
        Book newBook = new Book();
        newBook.setBookid(29);
        newBook.setTitle(newBookName);
//        Book newBook = bookService.findBookById(29);
//        newBook.setTitle(newBookName);

        Book updateBook = bookService.update(newBook, 29L);

        assertNotNull(updateBook);
        assertEquals(newBookName, updateBook.getTitle());
    }

    @Test
    public void deleteAll()
    {
        bookService.deleteAll();
        assertEquals(0L, bookService.findAll().size());
    }
}
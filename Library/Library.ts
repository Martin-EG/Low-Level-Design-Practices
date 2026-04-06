class LibraryService {
  constructor(
    private bookRepository: BookRepository,
    private loanRepository: LoanRepository
  ) {}

  borrowBook(isbn: string, user: User): void {
    const book = this.bookRepository.findBookByISBN(isbn);
    if(!book) throw new Error('Book not found...');
    if(!book.getIsAvailable()) throw new Error("Book not available...");

    book.borrow();
    this.loanRepository.add(new Loan(book, user));
    console.log(`${book.getTitle()} was borrowed by ${user.getName()}`);
  }

  returnBook(isbn: string): void {
    const book = this.bookRepository.findBookByISBN(isbn);
    if(!book) throw new Error('Book not found...');

    book.return();
    console.log(`${book.getTitle()} was returned...`);
  }

  getUserLoans(user: User): Loan[] {
    return this.loanRepository.findLoansByUser(user);
  }
}

class BookRepository {
  private books: Book[] = [];

  add(book: Book): void {
    this.books.push(book);
  }

  findBooksByTitle(title: string): Book[] {
    return this.books.filter((book: Book) => book.getTitle() === title)
  }

  findBooksByAuthor(author: string): Book[] {
    return this.books.filter((book: Book) => book.getAuthor() === author);
  }

  findBookByISBN(isbn: string): Book | undefined {
    return this.books.filter((book: Book) => book.getISBN() === isbn)[0];
  }

  getAll(): Book[] { return [...this.books]; }
}

class LoanRepository {
  private loans: Loan[] = [];

  add(loan: Loan): void { this.loans.push(loan); }

  findLoansByBookTitle(title: string): Loan[] {
    return this.loans.filter(
      (loan: Loan) => loan.getBook().getTitle() === title
    );
  }

  findLoansByUser(user: User): Loan[] {
    return this.loans.filter(
      (loan: Loan) => loan.getLoanBy().getId() === user.getId()
    );
  }
}

class Book {
  private title: string;
  private author: string;
  private isbn: string;
  private isAvailable: boolean = true;

  constructor(title: string, author: string, isbn: string) {
    this.title = title;
    this.author = author;
    this.isbn = isbn;
  }

  getTitle(): string { return this.title; }
  getAuthor(): string { return this.author; }
  getISBN(): string { return this.isbn; }
  getIsAvailable(): boolean { return this.isAvailable; }

  borrow(): void {
    if(this.isAvailable === false) throw new Error('It is already borrowed');
    this.isAvailable = false;
  }

  return(): void {
    if(this.isAvailable === true) throw new Error('It is not borrowed yet');
    this.isAvailable = true;
  }
}

class User {
  private name: string;
  private id: string;

  constructor(name: string, id: string) {
    this.name = name;
    this.id = id;
  }

  getName(): string { return this.name; }
  getId(): string { return this.id; }
}

class Loan {
  private book: Book;
  private loanBy: User;
  private borrowedAt: Date;

  constructor(book: Book, loanBy: User) {
    this.book = book;
    this.loanBy = loanBy;
    this.borrowedAt = new Date();
  }

  getBook(): Book { return this.book; }
  getLoanBy(): User { return this.loanBy; }
}


const bookRepository = new BookRepository();
const loanRepository = new LoanRepository();
const libraryService = new LibraryService(bookRepository, loanRepository);

bookRepository.add(new Book("Clean Code", "Robert Martin", "ISBN-001"));
bookRepository.add(new Book("The Pragmatic Programmer", "Hunt & Thomas", "ISBN-002"));

const user1 = new User("Alice", "USER-001");
const user2 = new User("Bob", "USER-002");

libraryService.borrowBook("ISBN-001", user1);
libraryService.borrowBook("ISBN-001", user2); // Error
libraryService.returnBook("ISBN-001");
libraryService.borrowBook("ISBN-001", user2); // Success
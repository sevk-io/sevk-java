> [!WARNING]
> Sevk is currently in private beta. This SDK is not yet available for public use.
> Join the waitlist at [sevk.io](https://sevk.io) to get early access.

<p align="center">
  <img src="https://sevk.io/logo.png" alt="Sevk" width="120" />
</p>

<h1 align="center">Sevk Java SDK</h1>

<p align="center">
  Official Java SDK for <a href="https://sevk.io">Sevk</a> email platform.
</p>

<p align="center">
  <a href="https://docs.sevk.io">Documentation</a> •
  <a href="https://sevk.io">Website</a>
</p>

## Installation

### Maven

```xml
<dependency>
    <groupId>io.sevk</groupId>
    <artifactId>sevk-java</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.sevk:sevk-java:0.1.0'
```

## Send Email

```java
import io.sevk.Sevk;
import io.sevk.types.SendEmailRequest;

public class Main {
    public static void main(String[] args) {
        Sevk sevk = new Sevk("your-api-key");

        sevk.emails().send(new SendEmailRequest()
            .to("recipient@example.com")
            .from("hello@yourdomain.com")
            .subject("Hello from Sevk!")
            .html("<h1>Welcome!</h1>")
        );
    }
}
```

## Send Email with Markup

```java
import io.sevk.Sevk;
import io.sevk.markup.MarkupRenderer;
import io.sevk.types.SendEmailRequest;

public class Main {
    public static void main(String[] args) {
        Sevk sevk = new Sevk("your-api-key");

        String html = MarkupRenderer.render("""
          <section padding="40px 20px" background-color="#f8f9fa">
            <container max-width="600px">
              <heading level="1" color="#1a1a1a">Welcome!</heading>
              <paragraph color="#666666">Thanks for signing up.</paragraph>
              <button href="https://example.com" background-color="#5227FF" color="#ffffff" padding="12px 24px">
                Get Started
              </button>
            </container>
          </section>
        """);

        sevk.emails().send(new SendEmailRequest()
            .to("recipient@example.com")
            .from("hello@yourdomain.com")
            .subject("Welcome!")
            .html(html)
        );
    }
}
```

## Documentation

For full documentation, visit [docs.sevk.io](https://docs.sevk.io)

## License

MIT

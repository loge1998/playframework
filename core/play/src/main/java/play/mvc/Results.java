/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package play.mvc;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import akka.util.ByteString;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.JsonNode;
import play.http.HttpEntity;
import play.twirl.api.Content;

import static play.mvc.Http.HeaderNames.LOCATION;
import static play.mvc.Http.Status.*;

/** Common results. */
public class Results {

  private static final String UTF8 = "utf-8";

  // -- Status

  /**
   * Generates a simple result.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @return the header-only result
   */
  public static StatusHeader status(int status) {
    return new StatusHeader(status);
  }

  /**
   * Generates a simple result.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the result's body content
   * @return the result
   */
  public static Result status(int status, Content content) {
    return status(status, content, UTF8);
  }

  /**
   * Generates a simple result.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the result's body content
   * @param charset the charset to encode the content with (e.g. "UTF-8")
   * @return the result
   */
  public static Result status(int status, Content content, String charset) {
    if (content == null) {
      throw new NullPointerException("Null content");
    }
    return status(status).sendEntity(HttpEntity.fromContent(content, charset));
  }

  /**
   * Generates a simple result.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the result's body content. It will be encoded as a UTF-8 string.
   * @return the result
   */
  public static Result status(int status, String content) {
    return status(status, content, UTF8);
  }

  /**
   * Generates a simple result.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the result's body content.
   * @param charset the charset in which to encode the content (e.g. "UTF-8")
   * @return the result
   */
  public static Result status(int status, String content, String charset) {
    if (content == null) {
      throw new NullPointerException("Null content");
    }
    return status(status).sendEntity(HttpEntity.fromString(content, charset));
  }

  /**
   * Generates a simple result with json content and UTF8 encoding.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the result's body content as a play-json object
   * @return the result
   */
  public static Result status(int status, JsonNode content) {
    return status(status, content, JsonEncoding.UTF8);
  }

  /**
   * Generates a simple result with json content.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the result's body content, as a play-json object
   * @param encoding the encoding into which the json should be encoded
   * @return the result
   */
  public static Result status(int status, JsonNode content, JsonEncoding encoding) {
    if (content == null) {
      throw new NullPointerException("Null content");
    }
    return status(status).sendJson(content, encoding);
  }

  /**
   * Generates a simple result with byte-array content.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the result's body content, as a byte array
   * @return the result
   */
  public static Result status(int status, byte[] content) {
    if (content == null) {
      throw new NullPointerException("Null content");
    }
    return status(status).sendBytes(content);
  }

  /**
   * Generates a simple result.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the result's body content
   * @return the result
   */
  public static Result status(int status, ByteString content) {
    if (content == null) {
      throw new NullPointerException("Null content");
    }
    return status(status).sendByteString(content);
  }

  /**
   * Generates a chunked result.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the input stream containing data to chunk over
   * @return the result
   */
  public static Result status(int status, InputStream content) {
    return status(status).sendInputStream(content);
  }

  /**
   * Generates a chunked result.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the input stream containing data to chunk over
   * @param contentLength the length of the provided content in bytes.
   * @return the result
   */
  public static Result status(int status, InputStream content, long contentLength) {
    return status(status).sendInputStream(content, contentLength);
  }

  /**
   * Generates a result with file contents.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the file to send
   * @return the result
   */
  public static Result status(int status, File content) {
    return status(status, content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a result with file contents.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the file to send
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result status(int status, File content, FileMimeTypes fileMimeTypes) {
    return status(status).sendFile(content, fileMimeTypes);
  }

  /**
   * Generates a result with file content.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the file to send
   * @param inline <code>true</code> to have it sent with inline Content-Disposition.
   * @return the result
   */
  public static Result status(int status, File content, boolean inline) {
    return status(status, content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a result with file content.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the file to send
   * @param inline <code>true</code> to have it sent with inline Content-Disposition.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result status(
      int status, File content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(status).sendFile(content, inline, fileMimeTypes);
  }

  /**
   * Generates a result.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the file to send
   * @param fileName the name that the client should receive this file as
   * @return the result
   */
  public static Result status(int status, File content, String fileName) {
    return status(status, content, fileName, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a result.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the file to send
   * @param fileName the name that the client should receive this file as
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result status(
      int status, File content, String fileName, FileMimeTypes fileMimeTypes) {
    return status(status).sendFile(content, fileName, fileMimeTypes);
  }

  /**
   * Generates a result.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the file to send
   * @param inline <code>true</code> to have it sent with inline Content-Disposition.
   * @param fileName the name that the client should receive this file as
   * @return the result
   */
  public static Result status(int status, File content, boolean inline, String fileName) {
    return status(status).sendFile(content, inline, fileName);
  }

  /**
   * Generates a result.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the file to send
   * @param inline <code>true</code> to have it sent with inline Content-Disposition.
   * @param fileName the name that the client should receive this file as
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result status(
      int status, File content, boolean inline, String fileName, FileMimeTypes fileMimeTypes) {
    return status(status).sendFile(content, inline, fileName, fileMimeTypes);
  }

  /**
   * Generates a result with path contents.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the path to send
   * @return the result
   */
  public static Result status(int status, Path content) {
    return status(status, content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a result with path contents.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the path to send
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result status(int status, Path content, FileMimeTypes fileMimeTypes) {
    return status(status).sendPath(content, fileMimeTypes);
  }

  /**
   * Generates a result with path content.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the path to send
   * @param inline <code>true</code> to have it sent with inline Content-Disposition.
   * @return the result
   */
  public static Result status(int status, Path content, boolean inline) {
    return status(status, content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a result with path content.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the path to send
   * @param inline <code>true</code> to have it sent with inline Content-Disposition.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result status(
      int status, Path content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(status).sendPath(content, inline, fileMimeTypes);
  }

  /**
   * Generates a result.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the path to send
   * @param fileName the name that the client should receive this path as
   * @return the result
   */
  public static Result status(int status, Path content, String fileName) {
    return status(status, content, fileName, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a result.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the path to send
   * @param fileName the name that the client should receive this path as
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result status(
      int status, Path content, String fileName, FileMimeTypes fileMimeTypes) {
    return status(status).sendPath(content, fileName, fileMimeTypes);
  }

  /**
   * Generates a result.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the path to send
   * @param inline <code>true</code> to have it sent with inline Content-Disposition.
   * @param fileName the name that the client should receive this path as
   * @return the result
   */
  public static Result status(int status, Path content, boolean inline, String fileName) {
    return status(status).sendPath(content, inline, fileName);
  }

  /**
   * Generates a result.
   *
   * @param status the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
   * @param content the path to send
   * @param inline <code>true</code> to have it sent with inline Content-Disposition.
   * @param fileName the name that the client should receive this path as
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result status(
      int status, Path content, boolean inline, String fileName, FileMimeTypes fileMimeTypes) {
    return status(status).sendPath(content, inline, fileName, fileMimeTypes);
  }

  /**
   * Generates a 204 No Content result.
   *
   * @return the result
   */
  public static StatusHeader noContent() {
    return new StatusHeader(NO_CONTENT);
  }

  //////////////////////////////////////////////////////
  // EVERYTHING BELOW HERE IS GENERATED
  //
  // See https://github.com/jroper/play-source-generator
  //////////////////////////////////////////////////////

  /**
   * Generates a 200 OK result.
   *
   * @return the result
   */
  public static StatusHeader ok() {
    return new StatusHeader(OK);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content the HTTP response body
   * @return the result
   */
  public static Result ok(Content content) {
    return status(OK, content);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result ok(Content content, String charset) {
    return status(OK, content, charset);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content HTTP response body, encoded as a UTF-8 string
   * @return the result
   */
  public static Result ok(String content) {
    return status(OK, content);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result ok(String content, String charset) {
    return status(OK, content, charset);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content the result's body content as a play-json object. It will be encoded as a UTF-8
   *     string.
   * @return the result
   */
  public static Result ok(JsonNode content) {
    return status(OK, content);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content the result's body content as a play-json object
   * @param encoding the encoding into which the json should be encoded
   * @return the result
   */
  public static Result ok(JsonNode content, JsonEncoding encoding) {
    return status(OK, content, encoding);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content the result's body content
   * @return the result
   */
  public static Result ok(byte[] content) {
    return status(OK, content);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content the input stream containing data to chunk over
   * @return the result
   */
  public static Result ok(InputStream content) {
    return status(OK, content);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content the input stream containing data to chunk over
   * @param contentLength the length of the provided content in bytes.
   * @return the result
   */
  public static Result ok(InputStream content, long contentLength) {
    return status(OK, content, contentLength);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content The file to send.
   * @return the result
   */
  public static Result ok(File content) {
    return ok(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content The file to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result ok(File content, FileMimeTypes fileMimeTypes) {
    return status(OK, content, fileMimeTypes);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result ok(File content, boolean inline) {
    return ok(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result ok(File content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(OK, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result ok(File content, String filename) {
    return ok(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result ok(File content, String filename, FileMimeTypes fileMimeTypes) {
    return status(OK, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result ok(File content, boolean inline, String filename) {
    return status(OK, content, inline, filename);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result ok(
      File content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(OK, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content The path to send.
   * @return the result
   */
  public static Result ok(Path content) {
    return ok(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content The path to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result ok(Path content, FileMimeTypes fileMimeTypes) {
    return status(OK, content, fileMimeTypes);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result ok(Path content, boolean inline) {
    return ok(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result ok(Path content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(OK, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result ok(Path content, String filename) {
    return ok(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result ok(Path content, String filename, FileMimeTypes fileMimeTypes) {
    return status(OK, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result ok(Path content, boolean inline, String filename) {
    return status(OK, content, inline, filename);
  }

  /**
   * Generates a 200 OK result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result ok(
      Path content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(OK, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 201 Created result.
   *
   * @return the result
   */
  public static StatusHeader created() {
    return new StatusHeader(CREATED);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content the HTTP response body
   * @return the result
   */
  public static Result created(Content content) {
    return status(CREATED, content);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result created(Content content, String charset) {
    return status(CREATED, content, charset);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content HTTP response body, encoded as a UTF-8 string
   * @return the result
   */
  public static Result created(String content) {
    return status(CREATED, content);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result created(String content, String charset) {
    return status(CREATED, content, charset);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content the result's body content as a play-json object. It will be encoded as a UTF-8
   *     string.
   * @return the result
   */
  public static Result created(JsonNode content) {
    return status(CREATED, content);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content the result's body content as a play-json object
   * @param encoding the encoding into which the json should be encoded
   * @return the result
   */
  public static Result created(JsonNode content, JsonEncoding encoding) {
    return status(CREATED, content, encoding);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content the result's body content
   * @return the result
   */
  public static Result created(byte[] content) {
    return status(CREATED, content);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content the input stream containing data to chunk over
   * @return the result
   */
  public static Result created(InputStream content) {
    return status(CREATED, content);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content the input stream containing data to chunk over
   * @param contentLength the length of the provided content in bytes.
   * @return the result
   */
  public static Result created(InputStream content, long contentLength) {
    return status(CREATED, content, contentLength);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content The file to send.
   * @return the result
   */
  public static Result created(File content) {
    return created(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content The file to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result created(File content, FileMimeTypes fileMimeTypes) {
    return status(CREATED, content, fileMimeTypes);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result created(File content, boolean inline) {
    return created(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result created(File content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(CREATED, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result created(File content, String filename) {
    return created(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result created(File content, String filename, FileMimeTypes fileMimeTypes) {
    return status(CREATED, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result created(File content, boolean inline, String filename) {
    return status(CREATED, content, inline, filename);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result created(
      File content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(CREATED, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content The path to send.
   * @return the result
   */
  public static Result created(Path content) {
    return created(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content The path to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result created(Path content, FileMimeTypes fileMimeTypes) {
    return status(CREATED, content, fileMimeTypes);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result created(Path content, boolean inline) {
    return created(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result created(Path content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(CREATED, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result created(Path content, String filename) {
    return created(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result created(Path content, String filename, FileMimeTypes fileMimeTypes) {
    return status(CREATED, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result created(Path content, boolean inline, String filename) {
    return status(CREATED, content, inline, filename);
  }

  /**
   * Generates a 201 Created result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result created(
      Path content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(CREATED, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @return the result
   */
  public static StatusHeader badRequest() {
    return new StatusHeader(BAD_REQUEST);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content the HTTP response body
   * @return the result
   */
  public static Result badRequest(Content content) {
    return status(BAD_REQUEST, content);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result badRequest(Content content, String charset) {
    return status(BAD_REQUEST, content, charset);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content HTTP response body, encoded as a UTF-8 string
   * @return the result
   */
  public static Result badRequest(String content) {
    return status(BAD_REQUEST, content);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result badRequest(String content, String charset) {
    return status(BAD_REQUEST, content, charset);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content the result's body content as a play-json object. It will be encoded as a UTF-8
   *     string.
   * @return the result
   */
  public static Result badRequest(JsonNode content) {
    return status(BAD_REQUEST, content);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content the result's body content as a play-json object
   * @param encoding the encoding into which the json should be encoded
   * @return the result
   */
  public static Result badRequest(JsonNode content, JsonEncoding encoding) {
    return status(BAD_REQUEST, content, encoding);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content the result's body content
   * @return the result
   */
  public static Result badRequest(byte[] content) {
    return status(BAD_REQUEST, content);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content the input stream containing data to chunk over
   * @return the result
   */
  public static Result badRequest(InputStream content) {
    return status(BAD_REQUEST, content);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content the input stream containing data to chunk over
   * @param contentLength the length of the provided content in bytes.
   * @return the result
   */
  public static Result badRequest(InputStream content, long contentLength) {
    return status(BAD_REQUEST, content, contentLength);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content The file to send.
   * @return the result
   */
  public static Result badRequest(File content) {
    return badRequest(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content The file to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result badRequest(File content, FileMimeTypes fileMimeTypes) {
    return status(BAD_REQUEST, content, fileMimeTypes);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result badRequest(File content, boolean inline) {
    return badRequest(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result badRequest(File content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(BAD_REQUEST, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result badRequest(File content, String filename) {
    return badRequest(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result badRequest(File content, String filename, FileMimeTypes fileMimeTypes) {
    return status(BAD_REQUEST, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result badRequest(File content, boolean inline, String filename) {
    return status(BAD_REQUEST, content, inline, filename);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result badRequest(
      File content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(BAD_REQUEST, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content The path to send.
   * @return the result
   */
  public static Result badRequest(Path content) {
    return badRequest(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content The path to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result badRequest(Path content, FileMimeTypes fileMimeTypes) {
    return status(BAD_REQUEST, content, fileMimeTypes);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result badRequest(Path content, boolean inline) {
    return badRequest(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result badRequest(Path content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(BAD_REQUEST, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result badRequest(Path content, String filename) {
    return badRequest(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result badRequest(Path content, String filename, FileMimeTypes fileMimeTypes) {
    return status(BAD_REQUEST, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result badRequest(Path content, boolean inline, String filename) {
    return status(BAD_REQUEST, content, inline, filename);
  }

  /**
   * Generates a 400 Bad Request result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result badRequest(
      Path content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(BAD_REQUEST, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @return the result
   */
  public static StatusHeader unauthorized() {
    return new StatusHeader(UNAUTHORIZED);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content the HTTP response body
   * @return the result
   */
  public static Result unauthorized(Content content) {
    return status(UNAUTHORIZED, content);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result unauthorized(Content content, String charset) {
    return status(UNAUTHORIZED, content, charset);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content HTTP response body, encoded as a UTF-8 string
   * @return the result
   */
  public static Result unauthorized(String content) {
    return status(UNAUTHORIZED, content);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result unauthorized(String content, String charset) {
    return status(UNAUTHORIZED, content, charset);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content the result's body content as a play-json object. It will be encoded as a UTF-8
   *     string.
   * @return the result
   */
  public static Result unauthorized(JsonNode content) {
    return status(UNAUTHORIZED, content);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content the result's body content as a play-json object
   * @param encoding the encoding into which the json should be encoded
   * @return the result
   */
  public static Result unauthorized(JsonNode content, JsonEncoding encoding) {
    return status(UNAUTHORIZED, content, encoding);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content the result's body content
   * @return the result
   */
  public static Result unauthorized(byte[] content) {
    return status(UNAUTHORIZED, content);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content the input stream containing data to chunk over
   * @return the result
   */
  public static Result unauthorized(InputStream content) {
    return status(UNAUTHORIZED, content);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content the input stream containing data to chunk over
   * @param contentLength the length of the provided content in bytes.
   * @return the result
   */
  public static Result unauthorized(InputStream content, long contentLength) {
    return status(UNAUTHORIZED, content, contentLength);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content The file to send.
   * @return the result
   */
  public static Result unauthorized(File content) {
    return unauthorized(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content The file to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result unauthorized(File content, FileMimeTypes fileMimeTypes) {
    return status(UNAUTHORIZED, content, fileMimeTypes);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result unauthorized(File content, boolean inline) {
    return unauthorized(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result unauthorized(File content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(UNAUTHORIZED, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result unauthorized(File content, String filename) {
    return unauthorized(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result unauthorized(File content, String filename, FileMimeTypes fileMimeTypes) {
    return status(UNAUTHORIZED, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result unauthorized(File content, boolean inline, String filename) {
    return status(UNAUTHORIZED, content, inline, filename);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result unauthorized(
      File content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(UNAUTHORIZED, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content The path to send.
   * @return the result
   */
  public static Result unauthorized(Path content) {
    return unauthorized(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content The path to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result unauthorized(Path content, FileMimeTypes fileMimeTypes) {
    return status(UNAUTHORIZED, content, fileMimeTypes);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result unauthorized(Path content, boolean inline) {
    return unauthorized(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result unauthorized(Path content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(UNAUTHORIZED, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result unauthorized(Path content, String filename) {
    return unauthorized(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result unauthorized(Path content, String filename, FileMimeTypes fileMimeTypes) {
    return status(UNAUTHORIZED, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result unauthorized(Path content, boolean inline, String filename) {
    return status(UNAUTHORIZED, content, inline, filename);
  }

  /**
   * Generates a 401 Unauthorized result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result unauthorized(
      Path content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(UNAUTHORIZED, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @return the result
   */
  public static StatusHeader paymentRequired() {
    return new StatusHeader(PAYMENT_REQUIRED);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content the HTTP response body
   * @return the result
   */
  public static Result paymentRequired(Content content) {
    return status(PAYMENT_REQUIRED, content);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result paymentRequired(Content content, String charset) {
    return status(PAYMENT_REQUIRED, content, charset);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content HTTP response body, encoded as a UTF-8 string
   * @return the result
   */
  public static Result paymentRequired(String content) {
    return status(PAYMENT_REQUIRED, content);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result paymentRequired(String content, String charset) {
    return status(PAYMENT_REQUIRED, content, charset);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content the result's body content as a play-json object. It will be encoded as a UTF-8
   *     string.
   * @return the result
   */
  public static Result paymentRequired(JsonNode content) {
    return status(PAYMENT_REQUIRED, content);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content the result's body content as a play-json object
   * @param encoding the encoding into which the json should be encoded
   * @return the result
   */
  public static Result paymentRequired(JsonNode content, JsonEncoding encoding) {
    return status(PAYMENT_REQUIRED, content, encoding);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content the result's body content
   * @return the result
   */
  public static Result paymentRequired(byte[] content) {
    return status(PAYMENT_REQUIRED, content);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content the input stream containing data to chunk over
   * @return the result
   */
  public static Result paymentRequired(InputStream content) {
    return status(PAYMENT_REQUIRED, content);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content the input stream containing data to chunk over
   * @param contentLength the length of the provided content in bytes.
   * @return the result
   */
  public static Result paymentRequired(InputStream content, long contentLength) {
    return status(PAYMENT_REQUIRED, content, contentLength);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content The file to send.
   * @return the result
   */
  public static Result paymentRequired(File content) {
    return paymentRequired(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content The file to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result paymentRequired(File content, FileMimeTypes fileMimeTypes) {
    return status(PAYMENT_REQUIRED, content, fileMimeTypes);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result paymentRequired(File content, boolean inline) {
    return paymentRequired(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result paymentRequired(File content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(PAYMENT_REQUIRED, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result paymentRequired(File content, String filename) {
    return paymentRequired(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result paymentRequired(File content, String filename, FileMimeTypes fileMimeTypes) {
    return status(PAYMENT_REQUIRED, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result paymentRequired(File content, boolean inline, String filename) {
    return status(PAYMENT_REQUIRED, content, inline, filename);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result paymentRequired(
      File content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(PAYMENT_REQUIRED, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content The path to send.
   * @return the result
   */
  public static Result paymentRequired(Path content) {
    return paymentRequired(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content The path to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result paymentRequired(Path content, FileMimeTypes fileMimeTypes) {
    return status(PAYMENT_REQUIRED, content, fileMimeTypes);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result paymentRequired(Path content, boolean inline) {
    return paymentRequired(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result paymentRequired(Path content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(PAYMENT_REQUIRED, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result paymentRequired(Path content, String filename) {
    return paymentRequired(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result paymentRequired(Path content, String filename, FileMimeTypes fileMimeTypes) {
    return status(PAYMENT_REQUIRED, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result paymentRequired(Path content, boolean inline, String filename) {
    return status(PAYMENT_REQUIRED, content, inline, filename);
  }

  /**
   * Generates a 402 Payment Required result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result paymentRequired(
      Path content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(PAYMENT_REQUIRED, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @return the result
   */
  public static StatusHeader forbidden() {
    return new StatusHeader(FORBIDDEN);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content the HTTP response body
   * @return the result
   */
  public static Result forbidden(Content content) {
    return status(FORBIDDEN, content);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result forbidden(Content content, String charset) {
    return status(FORBIDDEN, content, charset);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content HTTP response body, encoded as a UTF-8 string
   * @return the result
   */
  public static Result forbidden(String content) {
    return status(FORBIDDEN, content);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result forbidden(String content, String charset) {
    return status(FORBIDDEN, content, charset);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content the result's body content as a play-json object. It will be encoded as a UTF-8
   *     string.
   * @return the result
   */
  public static Result forbidden(JsonNode content) {
    return status(FORBIDDEN, content);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content the result's body content as a play-json object
   * @param encoding the encoding into which the json should be encoded
   * @return the result
   */
  public static Result forbidden(JsonNode content, JsonEncoding encoding) {
    return status(FORBIDDEN, content, encoding);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content the result's body content
   * @return the result
   */
  public static Result forbidden(byte[] content) {
    return status(FORBIDDEN, content);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content the input stream containing data to chunk over
   * @return the result
   */
  public static Result forbidden(InputStream content) {
    return status(FORBIDDEN, content);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content the input stream containing data to chunk over
   * @param contentLength the length of the provided content in bytes.
   * @return the result
   */
  public static Result forbidden(InputStream content, long contentLength) {
    return status(FORBIDDEN, content, contentLength);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content The file to send.
   * @return the result
   */
  public static Result forbidden(File content) {
    return forbidden(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content The file to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result forbidden(File content, FileMimeTypes fileMimeTypes) {
    return status(FORBIDDEN, content, fileMimeTypes);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result forbidden(File content, boolean inline) {
    return forbidden(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result forbidden(File content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(FORBIDDEN, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result forbidden(File content, String filename) {
    return forbidden(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result forbidden(File content, String filename, FileMimeTypes fileMimeTypes) {
    return status(FORBIDDEN, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result forbidden(File content, boolean inline, String filename) {
    return status(FORBIDDEN, content, inline, filename);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result forbidden(
      File content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(FORBIDDEN, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content The path to send.
   * @return the result
   */
  public static Result forbidden(Path content) {
    return forbidden(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content The path to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result forbidden(Path content, FileMimeTypes fileMimeTypes) {
    return status(FORBIDDEN, content, fileMimeTypes);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result forbidden(Path content, boolean inline) {
    return forbidden(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result forbidden(Path content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(FORBIDDEN, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result forbidden(Path content, String filename) {
    return forbidden(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result forbidden(Path content, String filename, FileMimeTypes fileMimeTypes) {
    return status(FORBIDDEN, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result forbidden(Path content, boolean inline, String filename) {
    return status(FORBIDDEN, content, inline, filename);
  }

  /**
   * Generates a 403 Forbidden result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result forbidden(
      Path content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(FORBIDDEN, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @return the result
   */
  public static StatusHeader notFound() {
    return new StatusHeader(NOT_FOUND);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content the HTTP response body
   * @return the result
   */
  public static Result notFound(Content content) {
    return status(NOT_FOUND, content);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result notFound(Content content, String charset) {
    return status(NOT_FOUND, content, charset);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content HTTP response body, encoded as a UTF-8 string
   * @return the result
   */
  public static Result notFound(String content) {
    return status(NOT_FOUND, content);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result notFound(String content, String charset) {
    return status(NOT_FOUND, content, charset);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content the result's body content as a play-json object. It will be encoded as a UTF-8
   *     string.
   * @return the result
   */
  public static Result notFound(JsonNode content) {
    return status(NOT_FOUND, content);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content the result's body content as a play-json object
   * @param encoding the encoding into which the json should be encoded
   * @return the result
   */
  public static Result notFound(JsonNode content, JsonEncoding encoding) {
    return status(NOT_FOUND, content, encoding);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content the result's body content
   * @return the result
   */
  public static Result notFound(byte[] content) {
    return status(NOT_FOUND, content);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content the input stream containing data to chunk over
   * @return the result
   */
  public static Result notFound(InputStream content) {
    return status(NOT_FOUND, content);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content the input stream containing data to chunk over
   * @param contentLength the length of the provided content in bytes.
   * @return the result
   */
  public static Result notFound(InputStream content, long contentLength) {
    return status(NOT_FOUND, content, contentLength);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content The file to send.
   * @return the result
   */
  public static Result notFound(File content) {
    return notFound(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content The file to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result notFound(File content, FileMimeTypes fileMimeTypes) {
    return status(NOT_FOUND, content, fileMimeTypes);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result notFound(File content, boolean inline) {
    return notFound(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result notFound(File content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(NOT_FOUND, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result notFound(File content, String filename) {
    return notFound(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result notFound(File content, String filename, FileMimeTypes fileMimeTypes) {
    return status(NOT_FOUND, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result notFound(File content, boolean inline, String filename) {
    return status(NOT_FOUND, content, inline, filename);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result notFound(
      File content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(NOT_FOUND, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content The path to send.
   * @return the result
   */
  public static Result notFound(Path content) {
    return notFound(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content The path to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result notFound(Path content, FileMimeTypes fileMimeTypes) {
    return status(NOT_FOUND, content, fileMimeTypes);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result notFound(Path content, boolean inline) {
    return notFound(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result notFound(Path content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(NOT_FOUND, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result notFound(Path content, String filename) {
    return notFound(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result notFound(Path content, String filename, FileMimeTypes fileMimeTypes) {
    return status(NOT_FOUND, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result notFound(Path content, boolean inline, String filename) {
    return status(NOT_FOUND, content, inline, filename);
  }

  /**
   * Generates a 404 Not Found result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result notFound(
      Path content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(NOT_FOUND, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @return the result
   */
  public static StatusHeader notAcceptable() {
    return new StatusHeader(NOT_ACCEPTABLE);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content the HTTP response body
   * @return the result
   */
  public static Result notAcceptable(Content content) {
    return status(NOT_ACCEPTABLE, content);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result notAcceptable(Content content, String charset) {
    return status(NOT_ACCEPTABLE, content, charset);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content HTTP response body, encoded as a UTF-8 string
   * @return the result
   */
  public static Result notAcceptable(String content) {
    return status(NOT_ACCEPTABLE, content);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result notAcceptable(String content, String charset) {
    return status(NOT_ACCEPTABLE, content, charset);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content the result's body content as a play-json object. It will be encoded as a UTF-8
   *     string.
   * @return the result
   */
  public static Result notAcceptable(JsonNode content) {
    return status(NOT_ACCEPTABLE, content);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content the result's body content as a play-json object
   * @param encoding the encoding into which the json should be encoded
   * @return the result
   */
  public static Result notAcceptable(JsonNode content, JsonEncoding encoding) {
    return status(NOT_ACCEPTABLE, content, encoding);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content the result's body content
   * @return the result
   */
  public static Result notAcceptable(byte[] content) {
    return status(NOT_ACCEPTABLE, content);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content the input stream containing data to chunk over
   * @return the result
   */
  public static Result notAcceptable(InputStream content) {
    return status(NOT_ACCEPTABLE, content);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content the input stream containing data to chunk over
   * @param contentLength the length of the provided content in bytes.
   * @return the result
   */
  public static Result notAcceptable(InputStream content, long contentLength) {
    return status(NOT_ACCEPTABLE, content, contentLength);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content The file to send.
   * @return the result
   */
  public static Result notAcceptable(File content) {
    return notAcceptable(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content The file to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result notAcceptable(File content, FileMimeTypes fileMimeTypes) {
    return status(NOT_ACCEPTABLE, content, fileMimeTypes);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result notAcceptable(File content, boolean inline) {
    return notAcceptable(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result notAcceptable(File content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(NOT_ACCEPTABLE, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result notAcceptable(File content, String filename) {
    return notAcceptable(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result notAcceptable(File content, String filename, FileMimeTypes fileMimeTypes) {
    return status(NOT_ACCEPTABLE, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result notAcceptable(File content, boolean inline, String filename) {
    return status(NOT_ACCEPTABLE, content, inline, filename);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result notAcceptable(
      File content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(NOT_ACCEPTABLE, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content The path to send.
   * @return the result
   */
  public static Result notAcceptable(Path content) {
    return notAcceptable(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content The path to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result notAcceptable(Path content, FileMimeTypes fileMimeTypes) {
    return status(NOT_ACCEPTABLE, content, fileMimeTypes);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result notAcceptable(Path content, boolean inline) {
    return notAcceptable(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result notAcceptable(Path content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(NOT_ACCEPTABLE, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result notAcceptable(Path content, String filename) {
    return notAcceptable(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result notAcceptable(Path content, String filename, FileMimeTypes fileMimeTypes) {
    return status(NOT_ACCEPTABLE, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result notAcceptable(Path content, boolean inline, String filename) {
    return status(NOT_ACCEPTABLE, content, inline, filename);
  }

  /**
   * Generates a 406 Not Acceptable result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result notAcceptable(
      Path content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(NOT_ACCEPTABLE, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @return the result
   */
  public static StatusHeader unsupportedMediaType() {
    return new StatusHeader(UNSUPPORTED_MEDIA_TYPE);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content the HTTP response body
   * @return the result
   */
  public static Result unsupportedMediaType(Content content) {
    return status(UNSUPPORTED_MEDIA_TYPE, content);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result unsupportedMediaType(Content content, String charset) {
    return status(UNSUPPORTED_MEDIA_TYPE, content, charset);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content HTTP response body, encoded as a UTF-8 string
   * @return the result
   */
  public static Result unsupportedMediaType(String content) {
    return status(UNSUPPORTED_MEDIA_TYPE, content);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result unsupportedMediaType(String content, String charset) {
    return status(UNSUPPORTED_MEDIA_TYPE, content, charset);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content the result's body content as a play-json object. It will be encoded as a UTF-8
   *     string.
   * @return the result
   */
  public static Result unsupportedMediaType(JsonNode content) {
    return status(UNSUPPORTED_MEDIA_TYPE, content);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content the result's body content as a play-json object
   * @param encoding the encoding into which the json should be encoded
   * @return the result
   */
  public static Result unsupportedMediaType(JsonNode content, JsonEncoding encoding) {
    return status(UNSUPPORTED_MEDIA_TYPE, content, encoding);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content the result's body content
   * @return the result
   */
  public static Result unsupportedMediaType(byte[] content) {
    return status(UNSUPPORTED_MEDIA_TYPE, content);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content the input stream containing data to chunk over
   * @return the result
   */
  public static Result unsupportedMediaType(InputStream content) {
    return status(UNSUPPORTED_MEDIA_TYPE, content);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content the input stream containing data to chunk over
   * @param contentLength the length of the provided content in bytes.
   * @return the result
   */
  public static Result unsupportedMediaType(InputStream content, long contentLength) {
    return status(UNSUPPORTED_MEDIA_TYPE, content, contentLength);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content The file to send.
   * @return the result
   */
  public static Result unsupportedMediaType(File content) {
    return unsupportedMediaType(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content The file to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result unsupportedMediaType(File content, FileMimeTypes fileMimeTypes) {
    return status(UNSUPPORTED_MEDIA_TYPE, content, fileMimeTypes);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result unsupportedMediaType(File content, boolean inline) {
    return unsupportedMediaType(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result unsupportedMediaType(
      File content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(UNSUPPORTED_MEDIA_TYPE, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result unsupportedMediaType(File content, String filename) {
    return unsupportedMediaType(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result unsupportedMediaType(
      File content, String filename, FileMimeTypes fileMimeTypes) {
    return status(UNSUPPORTED_MEDIA_TYPE, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result unsupportedMediaType(File content, boolean inline, String filename) {
    return status(UNSUPPORTED_MEDIA_TYPE, content, inline, filename);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result unsupportedMediaType(
      File content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(UNSUPPORTED_MEDIA_TYPE, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content The path to send.
   * @return the result
   */
  public static Result unsupportedMediaType(Path content) {
    return unsupportedMediaType(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content The path to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result unsupportedMediaType(Path content, FileMimeTypes fileMimeTypes) {
    return status(UNSUPPORTED_MEDIA_TYPE, content, fileMimeTypes);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result unsupportedMediaType(Path content, boolean inline) {
    return unsupportedMediaType(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result unsupportedMediaType(
      Path content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(UNSUPPORTED_MEDIA_TYPE, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result unsupportedMediaType(Path content, String filename) {
    return unsupportedMediaType(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result unsupportedMediaType(
      Path content, String filename, FileMimeTypes fileMimeTypes) {
    return status(UNSUPPORTED_MEDIA_TYPE, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result unsupportedMediaType(Path content, boolean inline, String filename) {
    return status(UNSUPPORTED_MEDIA_TYPE, content, inline, filename);
  }

  /**
   * Generates a 415 Unsupported Media Type result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result unsupportedMediaType(
      Path content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(UNSUPPORTED_MEDIA_TYPE, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @return the result
   */
  public static StatusHeader preconditionRequired() {
    return new StatusHeader(PRECONDITION_REQUIRED);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content the HTTP response body
   * @return the result
   */
  public static Result preconditionRequired(Content content) {
    return status(PRECONDITION_REQUIRED, content);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result preconditionRequired(Content content, String charset) {
    return status(PRECONDITION_REQUIRED, content, charset);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content HTTP response body, encoded as a UTF-8 string
   * @return the result
   */
  public static Result preconditionRequired(String content) {
    return status(PRECONDITION_REQUIRED, content);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result preconditionRequired(String content, String charset) {
    return status(PRECONDITION_REQUIRED, content, charset);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content the result's body content as a play-json object. It will be encoded as a UTF-8
   *     string.
   * @return the result
   */
  public static Result preconditionRequired(JsonNode content) {
    return status(PRECONDITION_REQUIRED, content);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content the result's body content as a play-json object
   * @param encoding the encoding into which the json should be encoded
   * @return the result
   */
  public static Result preconditionRequired(JsonNode content, JsonEncoding encoding) {
    return status(PRECONDITION_REQUIRED, content, encoding);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content the result's body content
   * @return the result
   */
  public static Result preconditionRequired(byte[] content) {
    return status(PRECONDITION_REQUIRED, content);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content the input stream containing data to chunk over
   * @return the result
   */
  public static Result preconditionRequired(InputStream content) {
    return status(PRECONDITION_REQUIRED, content);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content the input stream containing data to chunk over
   * @param contentLength the length of the provided content in bytes.
   * @return the result
   */
  public static Result preconditionRequired(InputStream content, long contentLength) {
    return status(PRECONDITION_REQUIRED, content, contentLength);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content The file to send.
   * @return the result
   */
  public static Result preconditionRequired(File content) {
    return preconditionRequired(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content The file to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result preconditionRequired(File content, FileMimeTypes fileMimeTypes) {
    return status(PRECONDITION_REQUIRED, content, fileMimeTypes);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result preconditionRequired(File content, boolean inline) {
    return preconditionRequired(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result preconditionRequired(
      File content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(PRECONDITION_REQUIRED, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result preconditionRequired(File content, String filename) {
    return preconditionRequired(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result preconditionRequired(
      File content, String filename, FileMimeTypes fileMimeTypes) {
    return status(PRECONDITION_REQUIRED, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result preconditionRequired(File content, boolean inline, String filename) {
    return status(PRECONDITION_REQUIRED, content, inline, filename);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result preconditionRequired(
      File content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(PRECONDITION_REQUIRED, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content The path to send.
   * @return the result
   */
  public static Result preconditionRequired(Path content) {
    return preconditionRequired(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content The path to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result preconditionRequired(Path content, FileMimeTypes fileMimeTypes) {
    return status(PRECONDITION_REQUIRED, content, fileMimeTypes);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result preconditionRequired(Path content, boolean inline) {
    return preconditionRequired(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result preconditionRequired(
      Path content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(PRECONDITION_REQUIRED, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result preconditionRequired(Path content, String filename) {
    return preconditionRequired(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result preconditionRequired(
      Path content, String filename, FileMimeTypes fileMimeTypes) {
    return status(PRECONDITION_REQUIRED, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result preconditionRequired(Path content, boolean inline, String filename) {
    return status(PRECONDITION_REQUIRED, content, inline, filename);
  }

  /**
   * Generates a 428 Precondition Required result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result preconditionRequired(
      Path content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(PRECONDITION_REQUIRED, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @return the result
   */
  public static StatusHeader tooManyRequests() {
    return new StatusHeader(TOO_MANY_REQUESTS);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content the HTTP response body
   * @return the result
   */
  public static Result tooManyRequests(Content content) {
    return status(TOO_MANY_REQUESTS, content);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result tooManyRequests(Content content, String charset) {
    return status(TOO_MANY_REQUESTS, content, charset);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content HTTP response body, encoded as a UTF-8 string
   * @return the result
   */
  public static Result tooManyRequests(String content) {
    return status(TOO_MANY_REQUESTS, content);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result tooManyRequests(String content, String charset) {
    return status(TOO_MANY_REQUESTS, content, charset);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content the result's body content as a play-json object. It will be encoded as a UTF-8
   *     string.
   * @return the result
   */
  public static Result tooManyRequests(JsonNode content) {
    return status(TOO_MANY_REQUESTS, content);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content the result's body content as a play-json object
   * @param encoding the encoding into which the json should be encoded
   * @return the result
   */
  public static Result tooManyRequests(JsonNode content, JsonEncoding encoding) {
    return status(TOO_MANY_REQUESTS, content, encoding);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content the result's body content
   * @return the result
   */
  public static Result tooManyRequests(byte[] content) {
    return status(TOO_MANY_REQUESTS, content);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content the input stream containing data to chunk over
   * @return the result
   */
  public static Result tooManyRequests(InputStream content) {
    return status(TOO_MANY_REQUESTS, content);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content the input stream containing data to chunk over
   * @param contentLength the length of the provided content in bytes.
   * @return the result
   */
  public static Result tooManyRequests(InputStream content, long contentLength) {
    return status(TOO_MANY_REQUESTS, content, contentLength);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content The file to send.
   * @return the result
   */
  public static Result tooManyRequests(File content) {
    return tooManyRequests(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content The file to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result tooManyRequests(File content, FileMimeTypes fileMimeTypes) {
    return status(TOO_MANY_REQUESTS, content, fileMimeTypes);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result tooManyRequests(File content, boolean inline) {
    return tooManyRequests(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result tooManyRequests(File content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(TOO_MANY_REQUESTS, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result tooManyRequests(File content, String filename) {
    return tooManyRequests(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result tooManyRequests(File content, String filename, FileMimeTypes fileMimeTypes) {
    return status(TOO_MANY_REQUESTS, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result tooManyRequests(File content, boolean inline, String filename) {
    return status(TOO_MANY_REQUESTS, content, inline, filename);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result tooManyRequests(
      File content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(TOO_MANY_REQUESTS, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content The path to send.
   * @return the result
   */
  public static Result tooManyRequests(Path content) {
    return tooManyRequests(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content The path to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result tooManyRequests(Path content, FileMimeTypes fileMimeTypes) {
    return status(TOO_MANY_REQUESTS, content, fileMimeTypes);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result tooManyRequests(Path content, boolean inline) {
    return tooManyRequests(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result tooManyRequests(Path content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(TOO_MANY_REQUESTS, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result tooManyRequests(Path content, String filename) {
    return tooManyRequests(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result tooManyRequests(Path content, String filename, FileMimeTypes fileMimeTypes) {
    return status(TOO_MANY_REQUESTS, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result tooManyRequests(Path content, boolean inline, String filename) {
    return status(TOO_MANY_REQUESTS, content, inline, filename);
  }

  /**
   * Generates a 429 Too Many Requests result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result tooManyRequests(
      Path content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(TOO_MANY_REQUESTS, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @return the result
   */
  public static StatusHeader requestHeaderFieldsTooLarge() {
    return new StatusHeader(REQUEST_HEADER_FIELDS_TOO_LARGE);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content the HTTP response body
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(Content content) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(Content content, String charset) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content, charset);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content HTTP response body, encoded as a UTF-8 string
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(String content) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(String content, String charset) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content, charset);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content the result's body content as a play-json object. It will be encoded as a UTF-8
   *     string.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(JsonNode content) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content the result's body content as a play-json object
   * @param encoding the encoding into which the json should be encoded
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(JsonNode content, JsonEncoding encoding) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content, encoding);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content the result's body content
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(byte[] content) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content the input stream containing data to chunk over
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(InputStream content) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content the input stream containing data to chunk over
   * @param contentLength the length of the provided content in bytes.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(InputStream content, long contentLength) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content, contentLength);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content The file to send.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(File content) {
    return requestHeaderFieldsTooLarge(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content The file to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(File content, FileMimeTypes fileMimeTypes) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content, fileMimeTypes);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(File content, boolean inline) {
    return requestHeaderFieldsTooLarge(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(
      File content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(File content, String filename) {
    return requestHeaderFieldsTooLarge(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(
      File content, String filename, FileMimeTypes fileMimeTypes) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(File content, boolean inline, String filename) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content, inline, filename);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(
      File content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content The path to send.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(Path content) {
    return requestHeaderFieldsTooLarge(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content The path to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(Path content, FileMimeTypes fileMimeTypes) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content, fileMimeTypes);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(Path content, boolean inline) {
    return requestHeaderFieldsTooLarge(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(
      Path content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(Path content, String filename) {
    return requestHeaderFieldsTooLarge(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(
      Path content, String filename, FileMimeTypes fileMimeTypes) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(Path content, boolean inline, String filename) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content, inline, filename);
  }

  /**
   * Generates a 431 Request Header Fields Too Large result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result requestHeaderFieldsTooLarge(
      Path content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(REQUEST_HEADER_FIELDS_TOO_LARGE, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @return the result
   */
  public static StatusHeader internalServerError() {
    return new StatusHeader(INTERNAL_SERVER_ERROR);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content the HTTP response body
   * @return the result
   */
  public static Result internalServerError(Content content) {
    return status(INTERNAL_SERVER_ERROR, content);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result internalServerError(Content content, String charset) {
    return status(INTERNAL_SERVER_ERROR, content, charset);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content HTTP response body, encoded as a UTF-8 string
   * @return the result
   */
  public static Result internalServerError(String content) {
    return status(INTERNAL_SERVER_ERROR, content);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result internalServerError(String content, String charset) {
    return status(INTERNAL_SERVER_ERROR, content, charset);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content the result's body content as a play-json object. It will be encoded as a UTF-8
   *     string.
   * @return the result
   */
  public static Result internalServerError(JsonNode content) {
    return status(INTERNAL_SERVER_ERROR, content);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content the result's body content as a play-json object
   * @param encoding the encoding into which the json should be encoded
   * @return the result
   */
  public static Result internalServerError(JsonNode content, JsonEncoding encoding) {
    return status(INTERNAL_SERVER_ERROR, content, encoding);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content the result's body content
   * @return the result
   */
  public static Result internalServerError(byte[] content) {
    return status(INTERNAL_SERVER_ERROR, content);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content the input stream containing data to chunk over
   * @return the result
   */
  public static Result internalServerError(InputStream content) {
    return status(INTERNAL_SERVER_ERROR, content);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content the input stream containing data to chunk over
   * @param contentLength the length of the provided content in bytes.
   * @return the result
   */
  public static Result internalServerError(InputStream content, long contentLength) {
    return status(INTERNAL_SERVER_ERROR, content, contentLength);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content The file to send.
   * @return the result
   */
  public static Result internalServerError(File content) {
    return internalServerError(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content The file to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result internalServerError(File content, FileMimeTypes fileMimeTypes) {
    return status(INTERNAL_SERVER_ERROR, content, fileMimeTypes);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result internalServerError(File content, boolean inline) {
    return internalServerError(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result internalServerError(
      File content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(INTERNAL_SERVER_ERROR, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result internalServerError(File content, String filename) {
    return internalServerError(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result internalServerError(
      File content, String filename, FileMimeTypes fileMimeTypes) {
    return status(INTERNAL_SERVER_ERROR, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result internalServerError(File content, boolean inline, String filename) {
    return status(INTERNAL_SERVER_ERROR, content, inline, filename);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result internalServerError(
      File content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(INTERNAL_SERVER_ERROR, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content The path to send.
   * @return the result
   */
  public static Result internalServerError(Path content) {
    return internalServerError(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content The path to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result internalServerError(Path content, FileMimeTypes fileMimeTypes) {
    return status(INTERNAL_SERVER_ERROR, content, fileMimeTypes);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result internalServerError(Path content, boolean inline) {
    return internalServerError(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result internalServerError(
      Path content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(INTERNAL_SERVER_ERROR, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result internalServerError(Path content, String filename) {
    return internalServerError(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result internalServerError(
      Path content, String filename, FileMimeTypes fileMimeTypes) {
    return status(INTERNAL_SERVER_ERROR, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result internalServerError(Path content, boolean inline, String filename) {
    return status(INTERNAL_SERVER_ERROR, content, inline, filename);
  }

  /**
   * Generates a 500 Internal Server Error result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result internalServerError(
      Path content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(INTERNAL_SERVER_ERROR, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @return the result
   */
  public static StatusHeader networkAuthenticationRequired() {
    return new StatusHeader(NETWORK_AUTHENTICATION_REQUIRED);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content the HTTP response body
   * @return the result
   */
  public static Result networkAuthenticationRequired(Content content) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result networkAuthenticationRequired(Content content, String charset) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content, charset);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content HTTP response body, encoded as a UTF-8 string
   * @return the result
   */
  public static Result networkAuthenticationRequired(String content) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content the HTTP response body
   * @param charset the charset into which the content should be encoded (e.g. "UTF-8")
   * @return the result
   */
  public static Result networkAuthenticationRequired(String content, String charset) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content, charset);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content the result's body content as a play-json object. It will be encoded as a UTF-8
   *     string.
   * @return the result
   */
  public static Result networkAuthenticationRequired(JsonNode content) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content the result's body content as a play-json object
   * @param encoding the encoding into which the json should be encoded
   * @return the result
   */
  public static Result networkAuthenticationRequired(JsonNode content, JsonEncoding encoding) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content, encoding);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content the result's body content
   * @return the result
   */
  public static Result networkAuthenticationRequired(byte[] content) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content the input stream containing data to chunk over
   * @return the result
   */
  public static Result networkAuthenticationRequired(InputStream content) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content the input stream containing data to chunk over
   * @param contentLength the length of the provided content in bytes.
   * @return the result
   */
  public static Result networkAuthenticationRequired(InputStream content, long contentLength) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content, contentLength);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content The file to send.
   * @return the result
   */
  public static Result networkAuthenticationRequired(File content) {
    return networkAuthenticationRequired(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content The file to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result networkAuthenticationRequired(File content, FileMimeTypes fileMimeTypes) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content, fileMimeTypes);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result networkAuthenticationRequired(File content, boolean inline) {
    return networkAuthenticationRequired(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result networkAuthenticationRequired(
      File content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result networkAuthenticationRequired(File content, String filename) {
    return networkAuthenticationRequired(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content The file to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result networkAuthenticationRequired(
      File content, String filename, FileMimeTypes fileMimeTypes) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result networkAuthenticationRequired(
      File content, boolean inline, String filename) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content, inline, filename);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content The file to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result networkAuthenticationRequired(
      File content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content The path to send.
   * @return the result
   */
  public static Result networkAuthenticationRequired(Path content) {
    return networkAuthenticationRequired(content, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content The path to send.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result networkAuthenticationRequired(Path content, FileMimeTypes fileMimeTypes) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content, fileMimeTypes);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @return the result
   */
  public static Result networkAuthenticationRequired(Path content, boolean inline) {
    return networkAuthenticationRequired(content, inline, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result networkAuthenticationRequired(
      Path content, boolean inline, FileMimeTypes fileMimeTypes) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content, inline, fileMimeTypes);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result networkAuthenticationRequired(Path content, String filename) {
    return networkAuthenticationRequired(content, filename, StaticFileMimeTypes.fileMimeTypes());
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content The path to send.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result networkAuthenticationRequired(
      Path content, String filename, FileMimeTypes fileMimeTypes) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content, filename, fileMimeTypes);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @return the result
   */
  public static Result networkAuthenticationRequired(
      Path content, boolean inline, String filename) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content, inline, filename);
  }

  /**
   * Generates a 511 Network Authentication Required result.
   *
   * @param content The path to send.
   * @param inline Whether the file should be sent inline, or as an attachment.
   * @param filename The name to send the file as.
   * @param fileMimeTypes Used for file type mapping.
   * @return the result
   */
  public static Result networkAuthenticationRequired(
      Path content, boolean inline, String filename, FileMimeTypes fileMimeTypes) {
    return status(NETWORK_AUTHENTICATION_REQUIRED, content, inline, filename, fileMimeTypes);
  }

  /**
   * Generates a 301 Moved Permanently result.
   *
   * @param url The url to redirect.
   * @return the result
   */
  public static Result movedPermanently(String url) {
    return new Result(MOVED_PERMANENTLY, Collections.singletonMap(LOCATION, url));
  }

  /**
   * Generates a 301 Moved Permanently result.
   *
   * @param call Call defining the url to redirect (typically comes from reverse router).
   * @return the result
   */
  public static Result movedPermanently(Call call) {
    return new Result(MOVED_PERMANENTLY, Collections.singletonMap(LOCATION, call.path()));
  }

  /**
   * Generates a 302 Found result.
   *
   * @param url The url to redirect.
   * @return the result
   */
  public static Result found(String url) {
    return new Result(FOUND, Collections.singletonMap(LOCATION, url));
  }

  /**
   * Generates a 302 Found result.
   *
   * @param call Call defining the url to redirect (typically comes from reverse router).
   * @return the result
   */
  public static Result found(Call call) {
    return new Result(FOUND, Collections.singletonMap(LOCATION, call.path()));
  }

  /**
   * Generates a 303 See Other result.
   *
   * @param url The url to redirect.
   * @return the result
   */
  public static Result seeOther(String url) {
    return new Result(SEE_OTHER, Collections.singletonMap(LOCATION, url));
  }

  /**
   * Generates a 303 See Other result.
   *
   * @param call Call defining the url to redirect (typically comes from reverse router).
   * @return the result
   */
  public static Result seeOther(Call call) {
    return new Result(SEE_OTHER, Collections.singletonMap(LOCATION, call.path()));
  }

  /**
   * Generates a 303 See Other result.
   *
   * @param url The url to redirect.
   * @return the result
   */
  public static Result redirect(String url) {
    return new Result(SEE_OTHER, Collections.singletonMap(LOCATION, url));
  }

  /**
   * Generates a 303 See Other result.
   *
   * @param url The url to redirect
   * @param queryStringParams queryString parameters to add to the queryString
   * @return the result
   */
  public static Result redirect(String url, Map<String, List<String>> queryStringParams) {
    String fullUrl = addQueryStringParams(url, queryStringParams);
    return new Result(SEE_OTHER, Collections.singletonMap(LOCATION, fullUrl));
  }

  /**
   * Encodes and adds the query params to the given url
   * @param url
   * @param queryStringParams
   * @return
   */
  public static String addQueryStringParams(
      String url, Map<String, List<String>> queryStringParams) {
    if (queryStringParams.isEmpty()) {
      return url;
    } else {
      String queryString =
          queryStringParams.entrySet().stream()
              .flatMap(
                  entry ->
                      entry.getValue().stream()
                          .map(
                              parameterValue ->
                                  URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
                                      + "="
                                      + URLEncoder.encode(parameterValue, StandardCharsets.UTF_8)))
              .collect(Collectors.joining("&"));

      return url + (url.contains("?") ? "&" : "?") + queryString;
    }
  }

  /**
   * Generates a 303 See Other result.
   *
   * @param call Call defining the url to redirect (typically comes from reverse router).
   * @return the result
   */
  public static Result redirect(Call call) {
    return new Result(SEE_OTHER, Collections.singletonMap(LOCATION, call.path()));
  }

  /**
   * Generates a 307 Temporary Redirect result.
   *
   * @param url The url to redirect.
   * @return the result
   */
  public static Result temporaryRedirect(String url) {
    return new Result(TEMPORARY_REDIRECT, Collections.singletonMap(LOCATION, url));
  }

  /**
   * Generates a 307 Temporary Redirect result.
   *
   * @param call Call defining the url to redirect (typically comes from reverse router).
   * @return the result
   */
  public static Result temporaryRedirect(Call call) {
    return new Result(TEMPORARY_REDIRECT, Collections.singletonMap(LOCATION, call.path()));
  }

  /**
   * Generates a 308 Permanent Redirect result.
   *
   * @param url The url to redirect.
   * @return the result
   */
  public static Result permanentRedirect(String url) {
    return new Result(PERMANENT_REDIRECT, Collections.singletonMap(LOCATION, url));
  }

  /**
   * Generates a 308 Permanent Redirect result.
   *
   * @param call Call defining the url to redirect (typically comes from reverse router).
   * @return the result
   */
  public static Result permanentRedirect(Call call) {
    return new Result(PERMANENT_REDIRECT, Collections.singletonMap(LOCATION, call.path()));
  }
}

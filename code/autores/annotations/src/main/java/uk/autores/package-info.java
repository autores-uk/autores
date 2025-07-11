// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0

/**
 * <p>
 * An annotation driven library for working with embedded resource files.
 * This package contains the core library functionality.
 * See other packages for annotation processing and extensibility.
 * </p>
 * <h2>AUTORES</h2>
 *
 * <h3>Usage</h3>
 *
 * <ol>
 *     <li>
 *         Add {@code import uk.autores.*;}
 *     </li>
 *     <li>
 *         Annotate a package ({@link java.lang.annotation.ElementType#PACKAGE})
 *         or type ({@link java.lang.annotation.ElementType#TYPE}) with one
 *         of the provided annotations
 *     </li>
 *     <li>
 *         Set {@code String[] values()} to the resource file names
 *     </li>
 * </ol>
 *
 * <table border="1">
 *     <caption>Annotation Summary</caption>
 *     <tr>
 *         <th>Annotation</th>
 *         <th>Consumes</th>
 *         <th>Generates</th>
 *         <th>Description</th>
 *     </tr>
 *     <tr>
 *         <td>{@link uk.autores.ByteArrays}</td>
 *         <td>any file</td>
 *         <td>class for all resources</td>
 *         <td>Generate class with method per file that returns <code>byte[]</code>.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link uk.autores.InputStreams}</td>
 *         <td>any file</td>
 *         <td>class for all resources</td>
 *         <td>Generate single class with one {@link java.io.InputStream} method per file.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link uk.autores.Keys}</td>
 *         <td>properties</td>
 *         <td>class per resource</td>
 *         <td>Generate one constants class with keys per {@link java.util.Properties} file.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link uk.autores.Messages}</td>
 *         <td>properties</td>
 *         <td>class per resource</td>
 *         <td>Generate one localized, formatting message generator class per {@link java.util.Properties} file.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link uk.autores.Texts}</td>
 *         <td>text files</td>
 *         <td>class for all resources</td>
 *         <td>Generate class with method per text file that returns {@link java.lang.String}.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link uk.autores.ResourceFiles}</td>
 *         <td>any file</td>
 *         <td>nothing (default)</td>
 *         <td>Validates the resource exists. Can be used to specify a {@link uk.autores.handling.Handler} with custom behaviour.</td>
 *     </tr>
 * </table>
 *
 * <h3>Names</h3>
 *
 * <p>
 *     Generated type and member names are automatically derived from either the resource file name
 *     or the annotated package name. Generated names can be influenced by either using "name"
 *     properties (like {@link uk.autores.ByteArrays#name()}) or setting {@link uk.autores.Processing#namer()}.
 * </p>
 * <p>
 *     Custom names may be required where collisions occur or derived names are not valid identifiers.
 * </p>
 *
 * <h3>Retention</h3>
 *
 * <p>
 *     Annotations in this package have {@link java.lang.annotation.RetentionPolicy#SOURCE} retention.
 *     They are compile-time only.
 * </p>
 *
 * <h3>Links</h3>
 *
 * <ul>
 *     <li><a href="https://github.com/autores-uk/autores" target="_">Source code</a></li>
 *     <li><a href="https://autores.uk" target="_">Website</a></li>
 * </ul>
 */
package uk.autores;

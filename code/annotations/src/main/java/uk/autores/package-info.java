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
 *         <th>Description</th>
 *     </tr>
 *     <tr>
 *         <td>{@link uk.autores.ByteArrayResources}</td>
 *         <td>Generate one class per file that returns <code>byte[]</code>.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link uk.autores.InputStreamResources}</td>
 *         <td>Generate single class with one {@link java.io.InputStream} method per file.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link uk.autores.KeyedResources}</td>
 *         <td>Generate one constants class with keys per {@link java.util.Properties} file.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link uk.autores.MessageResources}</td>
 *         <td>Generate one localized, formatting message generator class per {@link java.util.Properties} file.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link uk.autores.StringResources}</td>
 *         <td>Generate one class per text file that returns {@link java.lang.String}.</td>
 *     </tr>
 * </table>
 *
 * <h3>Links</h3>
 *
 * <ul>
 *     <li><a href="https://github.com/autores-uk/autores" target="_">Source code</a></li>
 *     <li><a href="https://autores.uk" target="_">Website</a></li>
 * </ul>
 */
package uk.autores;

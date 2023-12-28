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
 *         Place resource files in the appropriate place for the chosen build system.
 *         <ul><li>
 *             In Apache Maven this is typically under
 *             <a href="https://maven.apache.org/plugins/maven-resources-plugin/examples/resource-directory.html">
 *             <code>src/main/resources</code>
 *             </a>
 *          </li></ul>
 *     </li>
 *     <li>
 *         Annotate a type or package declaration with {@link uk.autores.ResourceFiles}.
 *     </li>
 *     <li>
 *         Set the resource file name in the {@link uk.autores.ResourceFiles#value()} array.
 *     </li>
 *     <li>
 *         Specify a {@link uk.autores.handling.Handler} for non-default behaviour.
 *     </li>
 * </ol>
 *
 * <h3>Resource Handlers</h3>
 *
 * <table border="1">
 *     <caption>provided handlers</caption>
 *     <thead>
 *         <tr>
 *             <th>{@link uk.autores.handling.Handler}</th>
 *             <th>Resources</th>
 *             <th>Configuration</th>
 *             <th>Description</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>{@link uk.autores.AssertResourceExists}</td>
 *             <td>any files</td>
 *             <td><em>(none)</em></td>
 *             <td>Default {@link uk.autores.handling.Handler} that validates resources exist.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.GenerateByteArraysFromFiles}</td>
 *             <td>any files</td>
 *             <td>{@link uk.autores.cfg.Visibility} {@link uk.autores.cfg.Strategy}</td>
 *             <td>Generates classes with a method that return a resource as a byte array.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.GenerateConstantsFromProperties}</td>
 *             <td>{@link java.util.Properties} files</td>
 *             <td>{@link uk.autores.cfg.Visibility}</td>
 *             <td>Generates classes containing String constants of the keys in a properties file.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.GenerateInputStreamsFromFiles}</td>
 *             <td>any files</td>
 *             <td>{@link uk.autores.cfg.Visibility} {@link uk.autores.cfg.Name}</td>
 *             <td>Generates a single class with methods for opening resource {@link java.io.InputStream}s.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.GenerateMessagesFromProperties}</td>
 *             <td>{@link java.util.Properties} files</td>
 *             <td>{@link uk.autores.cfg.Visibility} {@link uk.autores.cfg.Localize} {@link uk.autores.cfg.MissingKey} {@link uk.autores.cfg.Format}</td>
 *             <td>Generates classes with methods for returning property values including support for localization and message formatting.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.GenerateStringsFromText}</td>
 *             <td>text files</td>
 *             <td>{@link uk.autores.cfg.Visibility} {@link uk.autores.cfg.Encoding} {@link uk.autores.cfg.Strategy}</td>
 *             <td>Generates classes with a method that return a resource as a string.</td>
 *         </tr>
 *     </tbody>
 * </table>
 *
 * <h3>Links</h3>
 *
 * <ul>
 *     <li><a href="https://github.com/autores-uk/autores">Source code</a></li>
 *     <li><a href="https://autores.uk">Website</a></li>
 * </ul>
 */
package uk.autores;

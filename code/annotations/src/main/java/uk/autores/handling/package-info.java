// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0

/**
 * Use these types to extend the API.
 *
 * <h2>Usage</h2>
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
 *             <td>{@link uk.autores.handling.AssertResourceExists}</td>
 *             <td>any files</td>
 *             <td><em>(none)</em></td>
 *             <td>Default {@link uk.autores.handling.Handler} that validates resources exist.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.handling.GenerateByteArraysFromFiles}</td>
 *             <td>any files</td>
 *             <td>{@link uk.autores.handling.CfgVisibility} {@link uk.autores.handling.CfgStrategy}</td>
 *             <td>Generates classes with a method that return a resource as a byte array.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.handling.GenerateConstantsFromProperties}</td>
 *             <td>{@link java.util.Properties} files</td>
 *             <td>{@link uk.autores.handling.CfgVisibility}</td>
 *             <td>Generates classes containing String constants of the keys in a properties file.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.handling.GenerateInputStreamsFromFiles}</td>
 *             <td>any files</td>
 *             <td>{@link uk.autores.handling.CfgVisibility} {@link uk.autores.handling.CfgName}</td>
 *             <td>Generates a single class with methods for opening resource {@link java.io.InputStream}s.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.handling.GenerateMessagesFromProperties}</td>
 *             <td>{@link java.util.Properties} files</td>
 *             <td>{@link uk.autores.handling.CfgVisibility} {@link uk.autores.handling.CfgLocalize} {@link uk.autores.handling.CfgMissingKey} {@link uk.autores.handling.CfgFormat}</td>
 *             <td>Generates classes with methods for returning property values including support for localization and message formatting.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.handling.GenerateStringsFromText}</td>
 *             <td>text files</td>
 *             <td>{@link uk.autores.handling.CfgVisibility} {@link uk.autores.handling.CfgEncoding} {@link uk.autores.handling.CfgStrategy}</td>
 *             <td>Generates classes with a method that return a resource as a string.</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
package uk.autores.handling;

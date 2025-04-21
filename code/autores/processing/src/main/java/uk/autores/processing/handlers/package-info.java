// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0

/**
 * Resource handler implementations.
 *
 * <h2>Resource Handlers</h2>
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
 *             <td>{@link uk.autores.processing.handlers.GenerateByteArraysFromFiles}</td>
 *             <td>any files</td>
 *             <td>{@link uk.autores.processing.handlers.CfgVisibility} {@link uk.autores.processing.handlers.CfgStrategy}</td>
 *             <td>Generates classes with a method that return a resource as a byte array.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.processing.handlers.GenerateConstantsFromProperties}</td>
 *             <td>{@link java.util.Properties} files</td>
 *             <td>{@link uk.autores.processing.handlers.CfgVisibility}</td>
 *             <td>Generates classes containing String constants of the keys in a properties file.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.processing.handlers.GenerateInputStreamsFromFiles}</td>
 *             <td>any files</td>
 *             <td>{@link uk.autores.processing.handlers.CfgVisibility} {@link uk.autores.processing.handlers.CfgName}</td>
 *             <td>Generates a single class with methods for opening resource {@link java.io.InputStream}s.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.processing.handlers.GenerateMessagesFromProperties}</td>
 *             <td>{@link java.util.Properties} files</td>
 *             <td>{@link uk.autores.processing.handlers.CfgVisibility} {@link uk.autores.processing.handlers.CfgLocalize} {@link uk.autores.processing.handlers.CfgMissingKey} {@link uk.autores.processing.handlers.CfgFormat}</td>
 *             <td>Generates classes with methods for returning property values including support for localization and message formatting.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.processing.handlers.GenerateStringsFromText}</td>
 *             <td>text files</td>
 *             <td>{@link uk.autores.processing.handlers.CfgVisibility} {@link uk.autores.processing.handlers.CfgEncoding} {@link uk.autores.processing.handlers.CfgStrategy}</td>
 *             <td>Generates classes with a method that return a resource as a string.</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
package uk.autores.processing.handlers;

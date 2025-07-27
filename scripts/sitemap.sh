#!/bin/bash

TODAY=$(date -Idate)

XML="<?xml version='1.0' encoding='UTF-8'?>
<urlset xmlns='http://www.sitemaps.org/schemas/sitemap/0.9'>
   <url>
      <loc>https://autores.uk/</loc>
      <lastmod>${TODAY}</lastmod>
      <changefreq>monthly</changefreq>
   </url>
   <url>
      <loc>https://autores.uk/screenshots.html</loc>
      <lastmod>${TODAY}</lastmod>
      <changefreq>monthly</changefreq>
   </url>
   <url>
      <loc>https://autores.uk/tutorials/helloworld.html</loc>
      <lastmod>${TODAY}</lastmod>
      <changefreq>monthly</changefreq>
   </url>
   <url>
      <loc>https://autores.uk/tutorials/verify.html</loc>
      <lastmod>${TODAY}</lastmod>
      <changefreq>monthly</changefreq>
   </url>
   <url>
      <loc>https://autores.uk/tutorials/l10n.html</loc>
      <lastmod>${TODAY}</lastmod>
      <changefreq>monthly</changefreq>
   </url>
   <url>
      <loc>https://autores.uk/api/apidocs/index.html</loc>
      <lastmod>${TODAY}</lastmod>
      <changefreq>monthly</changefreq>
   </url>
   <url>
      <loc>https://autores.uk/impl/apidocs/index.html</loc>
      <lastmod>${TODAY}</lastmod>
      <changefreq>monthly</changefreq>
   </url>
</urlset>"

echo ${XML} > docs/sitemap.xml

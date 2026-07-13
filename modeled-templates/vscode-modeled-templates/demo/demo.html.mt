<!doctype html>
<html lang="en">
  <body class="${input.theme | (no-escape)}">
    %(var title string value: "People")
    <h1>${title}</h1>

    %(for-each input.people person)
      <article data-id="${person.id | (cast string)}">
        <h2>${person.name}</h2>
        %(if person.active)
          <strong>active</strong>
        %(else)
          <span>inactive</span>
        %(end)
      </article>
    %(end)

    #{This comment is consumed as raw SourceText.}
  </body>
</html>

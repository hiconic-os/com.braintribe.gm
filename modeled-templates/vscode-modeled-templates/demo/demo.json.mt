{
  "title": "${input.title}",
  "generatedAt": "${input.date}",
  "people": [
    %(for-each input.people person)
    {
      "name": "${person.name}",
      "active": ${person.active}
    }
    %(end)
  ]
}

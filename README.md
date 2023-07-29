Status: broken for now, only compiles with squint-cljs/defclass-2 branch and crashes on adding the component.


To start dev server:
1. Link squint-cljs from your dev folder into node_modules.
2. Compile index.cljs and move it to public/js/index.mjs
3. Start dev server. 

Compile required after any file change.
```
npm run compile && npm run deploy-local
```

Start web server
```
npm run start
```



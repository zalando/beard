[#ftl]
[#macro head title]
<head>
    <meta charset=utf-8/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <meta http-equiv=X-UA-Compatible content="IE=Edge"/>
    <title>${title} - Freemarker</title>
    <link rel="stylesheet" href="/webjars/bootstrap/3.0.1/css/bootstrap.min.css" media="screen" />
</head>
[/#macro]

[#macro pageTitle title]
<div class=page-header>
    <h1>${title} - Freemarker</h1>
</div>
[/#macro]

[#macro header]
<nav class="top-navi">
    <ul>
        <li>First Link</li>
        <li>Second Link</li>
        <li>Third Link</li>
    </ul>
</nav>
[/#macro]
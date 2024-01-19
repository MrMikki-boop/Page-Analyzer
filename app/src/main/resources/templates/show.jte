@import hexlet.code.dto.UrlPage
@import java.time.format.DateTimeFormatter
@param UrlPage page

@template.layout.page(page = page,
content = @`
    <div class="container-lg mt-5">
        <h1>Website: ${page.getUrl().getName()}</h1>

        <table class="table table-bordered table-hover mt-3">
            <tbody>
            <tr>
                <td>ID</td>
                <td>${page.getUrl().getId()}</td>
            </tr>
            <tr>
                <td>Name</td>
                <td>${page.getUrl().getName()}</td>
            </tr>
            <tr>
                <td>Created At</td>
                !{var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");}
                <td>${page.getUrl().getCreatedAt().toLocalDateTime().format(formatter)}</td>
            </tr>
            </tbody>
        </table>

        <h2 class="mt-5">Checks</h2>
        <form method="post" action="/urls/${page.getUrl().getId()}/checks">
            <button type="submit" class="btn btn-primary">Run Check</button>
        </form>

        <table class="table table-bordered table-hover mt-3">
            <thead>
            <th class="col-1">ID</th>
            <th class="col-1">Status Code</th>
            <th>title</th>
            <th>h1</th>
            <th>description</th>
            <th class="col-2">Check Date</th>
            </thead>
            <tbody>
            @for(var urlCheck : page.getUrlChecks())
                <tr>
                    <td>${urlCheck.getId()}</td>
                    <td>${urlCheck.getStatusCode()}</td>
                    <td>${urlCheck.getTitle()}</td>
                    <td>${urlCheck.getH1()}</td>
                    <td>${urlCheck.getDescription()}</td>
                    <td>${urlCheck.getCreatedAt().toLocalDateTime().format(formatter)}</td>
                </tr>
            @endfor

            </tbody>
        </table>
    </div>
`
)

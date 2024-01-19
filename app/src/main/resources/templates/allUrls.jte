@import hexlet.code.dto.UrlsPage
@import java.time.format.DateTimeFormatter
@param UrlsPage page

@template.layout.page(page = page,
content = @`
    <div class="container-lg mt-5">

        <h1>Urls</h1>

        <table class="table table-bordered table-hover mt-3">
            <thead>
            <tr>
                <th class="col-1">ID</th>
                <th>Name</th>
                <th class="col-2">Last Check</th>
                <th class="col-1">Status Code</th>
            </tr>
            </thead>
            <tbody>
            @for(var url : page.getUrls())
                !{var urlCheck = page.getUrlChecks().get(url.getId());}
                <tr>
                    <td>${url.getId()}</td>
                    <td>
                        <a href="/urls/${url.getId()}">${url.getName()}</a>
                    </td>
                    <td>
                        !{var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");}
                        ${urlCheck == null ? "" : urlCheck.getCreatedAt().toLocalDateTime().format(formatter)}
                    </td>
                    <td>
                        ${urlCheck == null ? "" : String.valueOf(urlCheck.getStatusCode())}
                    </td>
                </tr>
            @endfor
            </tbody>
        </table>

        <nav aria-label="Page navigation">
            <ul class="pagination justify-content-center mt-5">
                <li class="page-item disabled">
                    <a class="page-link" href="/urls?page=0">Previous</a>
                </li>
                <li>
                    <a class="page-link" href="/urls?page=1">1</a>
                </li>
                <li>
                    <a class="page-link" href="/urls?page=2">Next</a>
                </li>
            </ul>
        </nav>
    </div>
`
)

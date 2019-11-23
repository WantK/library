function logout() {
    window.location = "/";
}
function registerReader() {
    window.location.replace("/registerReader");
}

function manageReader() {
    window.location.replace("/manageReader");
}

function addBook() {
    window.location.replace("/insertBook");
}

// function searchBook() {
//     window.location.replace("/searchIndex");
// }

function manageBook() {
    window.location.replace("/searchIndex");
}

function borrowBook() {
    window.location.replace("/borrowIndex");
}

function returnBook() {
    window.location.replace("/returnIndex");
}

function showRecord() {
    window.location.replace("/recordIndex");
}


function selectBook() {
    var isbn = document.getElementById("isbn").value;


    $ .ajax({
        url:"/getBookInformation/isbn="+isbn,
        type:'POST',
        data:{
            _method:"POST"
        },
        dateType:"json",
        success:function (book) {
            document.getElementById("bookName").innerText = "bookName:"+book["title"];
            document.getElementById("bookAuthor").innerText = "bookAuthor:"+book["author"];
            document.getElementById("bookImg").setAttribute("style","display:inline");
            document.getElementById("bookImg").setAttribute("src",book["image"]);
            document.getElementById("bookPrice").innerText = "bookPrice:"+book["price"];
            document.getElementById("bookISBN").innerText = "bookISBN:"+isbn;

            document.getElementById("add Book").setAttribute("style","display:none");
            document.getElementById("bookCategoryTr").setAttribute("style",null);
            document.getElementById("bookCountTr").setAttribute("style",null);
            document.getElementById("bookFloorTr").setAttribute("style",null);
            document.getElementById("bookRoomTr").setAttribute("style",null);
            document.getElementById("bookShelfTr").setAttribute("style",null);
            document.getElementById("confirm add").setAttribute("style","display:inline;margin-left: 120px");
        }
    });
}

function addBook() {
    $.ajax({
        url: "/addBook",
        type:"POST",
        async : false,
        data:
            {
                _method:"POST",
                title: document.getElementById("bookName").innerText.split(":")[1],
                author: document.getElementById("bookAuthor").innerText.split(":")[1],
                image: document.getElementById("bookImg").src,
                price: document.getElementById("bookPrice").innerText.split(":")[1],
                isbn: document.getElementById("bookISBN").innerText.split(":")[1],
                count: document.getElementById("count").value,
                category:document.getElementById("category").value,
                floor:document.getElementById("floor").value,
                room:document.getElementById("room").value,
                shelf:document.getElementById("shelf").value
            },
        dataType:"text",
        success:function (result) {
            alert(result);
            window.location.replace("/showNewBooks/" + document.getElementById("bookISBN").innerText.split(":")[1]);
        }
    });

}

function deleteAnnounce(announceId) {
    $.ajax({
        url:"/deleteAnnounce",
        type:"POST",
        async:false,
        data:{
            _method:"PUT",
            announceId:announceId
        },
        success:window.location.reload(true)
    })
    
}
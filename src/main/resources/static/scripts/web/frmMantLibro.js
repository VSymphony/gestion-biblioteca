var categorias = [];

$(document).ready(function () {
    cargarCategorias();
});

function cargarCategorias() {
    $.ajax({
        url: '/categoria/listar',
        type: 'GET',
        dataType: 'json'
    }).done(function (resp) {
        categorias = resp.data || [];
        var sel = $('#categoriaLibro');
        sel.empty();
        categorias.forEach(function (c) {
            sel.append($('<option>').val(c.idcategoria).text(c.nombre));
        });
        listarLibros();
    }).fail(function () {
        mostrarMensaje('#mensajeLibro', 'Error al cargar las categorías', false);
    });
}

function listarLibros() {
    $.ajax({
        url: '/libro/listar',
        type: 'GET',
        dataType: 'json'
    }).done(function (resp) {
        var tbody = $('#tablaLibros tbody');
        tbody.empty();
        (resp.data || []).forEach(function (l) {
            var acciones = $('<td>')
                .append($('<button>')
                    .addClass('btn btn-sm btn-warning me-1')
                    .text('Editar')
                    .on('click', function () { editarLibro(l); }))
                .append($('<button>')
                    .addClass('btn btn-sm btn-danger')
                    .text('Eliminar')
                    .on('click', function () { eliminarLibro(l.idlibro); }));
            tbody.append(
                $('<tr>')
                    .append($('<td>').text(l.idlibro))
                    .append($('<td>').text(l.titulo || ''))
                    .append($('<td>').text(l.autor || ''))
                    .append($('<td>').text(l.categoria ? l.categoria.nombre : ''))
                    .append($('<td>').text(l.anio || ''))
                    .append($('<td>').text(l.stock == null ? '' : l.stock))
                    .append(acciones)
            );
        });
    }).fail(function () {
        mostrarMensaje('#mensajeLibro', 'Error al listar los libros', false);
    });
}

function nuevoLibro() {
    $('#idLibro').val('');
    $('#tituloLibro').val('');
    $('#autorLibro').val('');
    $('#isbnLibro').val('');
    $('#anioLibro').val('');
    $('#stockLibro').val('');
    if (categorias.length > 0) {
        $('#categoriaLibro').val(categorias[0].idcategoria);
    }
    $('#tituloModalLibro').text('Nuevo Libro');
    bootstrap.Modal.getOrCreateInstance(document.getElementById('modalLibro')).show();
}

function editarLibro(l) {
    $('#idLibro').val(l.idlibro);
    $('#tituloLibro').val(l.titulo);
    $('#autorLibro').val(l.autor);
    $('#isbnLibro').val(l.isbn);
    $('#anioLibro').val(l.anio);
    $('#stockLibro').val(l.stock);
    $('#categoriaLibro').val(l.categoria ? l.categoria.idcategoria : '');
    $('#tituloModalLibro').text('Editar Libro');
    bootstrap.Modal.getOrCreateInstance(document.getElementById('modalLibro')).show();
}

function grabarLibro() {
    $.ajax({
        url: '/libro/grabar',
        type: 'POST',
        data: {
            idlibro: $('#idLibro').val(),
            titulo: $('#tituloLibro').val(),
            autor: $('#autorLibro').val(),
            isbn: $('#isbnLibro').val(),
            anio: $('#anioLibro').val(),
            stock: $('#stockLibro').val(),
            idcategoria: $('#categoriaLibro').val(),
            _csrf: csrfToken()
        },
        dataType: 'json'
    }).done(function (resp) {
        mostrarMensaje('#mensajeLibro', resp.mensaje, resp.respuesta);
        if (resp.respuesta) {
            bootstrap.Modal.getOrCreateInstance(document.getElementById('modalLibro')).hide();
            listarLibros();
        }
    }).fail(function () {
        mostrarMensaje('#mensajeLibro', 'Error al grabar el libro', false);
    });
}

function eliminarLibro(idlibro) {
    if (!confirm('¿Eliminar el libro seleccionado?')) {
        return;
    }
    $.ajax({
        url: '/libro/eliminar',
        type: 'POST',
        data: {
            idlibro: idlibro,
            _csrf: csrfToken()
        },
        dataType: 'json'
    }).done(function (resp) {
        mostrarMensaje('#mensajeLibro', resp.mensaje, resp.respuesta);
        if (resp.respuesta) {
            listarLibros();
        }
    }).fail(function () {
        mostrarMensaje('#mensajeLibro', 'Error al eliminar el libro', false);
    });
}

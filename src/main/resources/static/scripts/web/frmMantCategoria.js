$(document).ready(function () {
    listarCategorias();
});

function listarCategorias() {
    $.ajax({
        url: '/categoria/listar',
        type: 'GET',
        dataType: 'json'
    }).done(function (resp) {
        var tbody = $('#tablaCategorias tbody');
        tbody.empty();
        (resp.data || []).forEach(function (c) {
            var acciones = $('<td>')
                .append($('<button>')
                    .addClass('btn btn-sm btn-warning me-1')
                    .text('Editar')
                    .on('click', function () { editarCategoria(c); }))
                .append($('<button>')
                    .addClass('btn btn-sm btn-danger')
                    .text('Eliminar')
                    .on('click', function () { eliminarCategoria(c.idcategoria); }));
            tbody.append(
                $('<tr>')
                    .append($('<td>').text(c.idcategoria))
                    .append($('<td>').text(c.nombre || ''))
                    .append($('<td>').text(c.descripcion || ''))
                    .append(acciones)
            );
        });
    }).fail(function () {
        mostrarMensaje('#mensajeCategoria', 'Error al listar las categorías', false);
    });
}

function nuevaCategoria() {
    $('#idCategoria').val('');
    $('#nombreCategoria').val('');
    $('#descripcionCategoria').val('');
    $('#tituloModalCategoria').text('Nueva Categoría');
    bootstrap.Modal.getOrCreateInstance(document.getElementById('modalCategoria')).show();
}

function editarCategoria(c) {
    $('#idCategoria').val(c.idcategoria);
    $('#nombreCategoria').val(c.nombre);
    $('#descripcionCategoria').val(c.descripcion);
    $('#tituloModalCategoria').text('Editar Categoría');
    bootstrap.Modal.getOrCreateInstance(document.getElementById('modalCategoria')).show();
}

function grabarCategoria() {
    $.ajax({
        url: '/categoria/grabar',
        type: 'POST',
        data: {
            idcategoria: $('#idCategoria').val(),
            nombre: $('#nombreCategoria').val(),
            descripcion: $('#descripcionCategoria').val(),
            _csrf: csrfToken()
        },
        dataType: 'json'
    }).done(function (resp) {
        mostrarMensaje('#mensajeCategoria', resp.mensaje, resp.respuesta);
        if (resp.respuesta) {
            bootstrap.Modal.getOrCreateInstance(document.getElementById('modalCategoria')).hide();
            listarCategorias();
        }
    }).fail(function () {
        mostrarMensaje('#mensajeCategoria', 'Error al grabar la categoría', false);
    });
}

function eliminarCategoria(idcategoria) {
    if (!confirm('¿Eliminar la categoría seleccionada?')) {
        return;
    }
    $.ajax({
        url: '/categoria/eliminar',
        type: 'POST',
        data: {
            idcategoria: idcategoria,
            _csrf: csrfToken()
        },
        dataType: 'json'
    }).done(function (resp) {
        mostrarMensaje('#mensajeCategoria', resp.mensaje, resp.respuesta);
        if (resp.respuesta) {
            listarCategorias();
        }
    }).fail(function () {
        mostrarMensaje('#mensajeCategoria', 'Error al eliminar la categoría', false);
    });
}

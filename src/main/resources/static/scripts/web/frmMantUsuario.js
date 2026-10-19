var roles = [];

$(document).ready(function () {
    cargarRoles();
});

function cargarRoles() {
    $.ajax({
        url: '/usuario/roles',
        type: 'GET',
        dataType: 'json'
    }).done(function (resp) {
        roles = resp.data || [];
        listarUsuarios();
    }).fail(function () {
        mostrarMensaje('#mensajeUsuario', 'Error al cargar los roles', false);
    });
}

function listarUsuarios() {
    $.ajax({
        url: '/usuario/listar',
        type: 'GET',
        dataType: 'json'
    }).done(function (resp) {
        var tbody = $('#tablaUsuarios tbody');
        tbody.empty();
        (resp.data || []).forEach(function (u) {
            tbody.append(filaUsuario(u));
        });
    }).fail(function () {
        mostrarMensaje('#mensajeUsuario', 'Error al listar los usuarios', false);
    });
}

function filaUsuario(u) {
    var selectRol = $('<select>').addClass('form-select form-select-sm');
    roles.forEach(function (r) {
        selectRol.append(
            $('<option>')
                .val(r.idrol)
                .text(r.nomrol)
                .prop('selected', u.rol && u.rol.idrol === r.idrol)
        );
    });
    selectRol.on('change', function () {
        cambiarRol(u.idusuario, $(this).val());
    });

    var estado = $('<span>')
        .addClass('badge ' + (u.activo ? 'text-bg-success' : 'text-bg-secondary'))
        .text(u.activo ? 'Activo' : 'Inactivo');

    var botonEstado = $('<button>')
        .addClass('btn btn-sm ' + (u.activo ? 'btn-outline-danger' : 'btn-outline-success'))
        .text(u.activo ? 'Desactivar' : 'Activar')
        .on('click', function () { cambiarEstado(u.idusuario, !u.activo); });

    var nombres = [u.nombres, u.apellidos].filter(Boolean).join(' ');

    return $('<tr>')
        .append($('<td>').text(u.nomusuario))
        .append($('<td>').text(nombres))
        .append($('<td>').text(u.email || ''))
        .append($('<td>').append(selectRol))
        .append($('<td>').append(estado))
        .append($('<td>').append(botonEstado));
}

function cambiarEstado(idusuario, activo) {
    $.ajax({
        url: '/usuario/estado',
        type: 'POST',
        data: {
            idusuario: idusuario,
            activo: activo,
            _csrf: csrfToken()
        },
        dataType: 'json'
    }).done(function (resp) {
        mostrarMensaje('#mensajeUsuario', resp.mensaje, resp.respuesta);
        if (resp.respuesta) {
            listarUsuarios();
        }
    }).fail(function () {
        mostrarMensaje('#mensajeUsuario', 'Error al cambiar el estado', false);
    });
}

function cambiarRol(idusuario, idrol) {
    $.ajax({
        url: '/usuario/rol',
        type: 'POST',
        data: {
            idusuario: idusuario,
            idrol: idrol,
            _csrf: csrfToken()
        },
        dataType: 'json'
    }).done(function (resp) {
        mostrarMensaje('#mensajeUsuario', resp.mensaje, resp.respuesta);
        if (resp.respuesta) {
            listarUsuarios();
        }
    }).fail(function () {
        mostrarMensaje('#mensajeUsuario', 'Error al cambiar el rol', false);
    });
}

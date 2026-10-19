function csrfToken() {
    return $('meta[name="csrf-token"]').attr('content');
}

function mostrarMensaje(selector, mensaje, exito) {
    $(selector).html(
        $('<div>')
            .addClass('alert alert-' + (exito ? 'success' : 'danger'))
            .text(mensaje)
    );
}

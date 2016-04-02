from PIL import Image as PILImage
from django.http import HttpResponse
from django.template.response import TemplateResponse

from main.models import Instruction, Image, Drawing


def get_latest_instruction(request):
    """
    Get the latest instruction
    :return:
    """
    i = Instruction().latest_instruction()
    path = i.render_instruction()

    try:
        with open(path, 'rb') as f:
            return HttpResponse(f.read(), content_type="image/png")
    except IOError:
        red = PILImage.new('RGBA', (1, 1), (255, 0, 0, 0))
        response = HttpResponse(content_type="image/jpeg")
        red.save(response, "JPEG")
        return response


def get_image(request):
    id = request.GET.get('id')
    image = Image.objects.get(pk=id)
    with open(image.filename, 'rb') as f:
        return HttpResponse(f.read(), content_type="png")


def create_new_instruction(request):
    """
    Create a new instruction. Can either be based on an existing instruction (using "instruction=<id>")
    or based on an image (using "image=<id>"). Instruction is stronger then image.
    """

    instruction_id = request.GET.get('instruction', None)
    image_id = request.GET.get('image', None)

    if instruction_id is not None:
        old_instruction = Instruction.objects.get(pk=instruction_id)
        instruction = old_instruction.copy()
        instruction.save()
    elif image_id is not None:
        # Create Instruction based on image
        instruction = Instruction()
        instruction.image = Image.objects.get(pk=image_id)
        instruction.save()
    else:
        return HttpResponse("You fucked up!")


def add_drawing(request):
    """
    Add a drawing to the latest instruction.
    Arguments are: x1, y1, x2, y2, shape
    """
    instruction = Instruction.latest_instruction()

    # Get all the parameters
    x1 = request.GET.get('x1', None)
    y1 = request.GET.get('y1', None)
    x2 = request.GET.get('x2', None)
    y2 = request.GET.get('y2', None)
    shape = request.GET.get('shape', None)

    # Validate parameters
    parameters = [x1, y1, x2, y2, shape]
    for par in parameters:
        if par is None:
            return HttpResponse("You fucked up!")

    drawing = Drawing()
    drawing.x1 = x1
    drawing.y1 = y1
    drawing.x2 = x2
    drawing.y2 = y2
    drawing.instruction = instruction
    drawing.save()


def main_app(request):
    context = None
    return TemplateResponse(request, "main/main_app.html", context)


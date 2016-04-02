from base64 import b64decode

from PIL import Image as PILImage
from django.http import HttpResponse, JsonResponse
from django.template.response import TemplateResponse
from django.views.decorators.csrf import csrf_exempt

from main.models import Instruction, Image


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
    id = request.GET.get('id', None)
    if id is None:
        # Return all images relevant images
        images = Image.get_relevant_images()

        # Transfer images to json
        images_data = []
        for image in images:
            im_dic = {
                'path': '/image?id=%d' % image.id
            }
            images_data.append(im_dic)
        return JsonResponse({'images': images_data})
    else:
        # Return the image
        image = Image.objects.get(pk=id)
        with open(image.filename, 'rb') as f:
            return HttpResponse(f.read(), content_type="png")


@csrf_exempt
def create_new_instruction(request):
    """
    Create a new instruction. Can either be based on an existing instruction (using "instruction=<id>")
    or based on an image (using "image=<id>"). Instruction is stronger then image.
    """

    instruction = Instruction()
    instruction.filepath = "/foo"
    instruction.save()

    new_instruction_file = request.POST.get('instruction', None)
    if new_instruction_file is None:
        return HttpResponse("You fucked up!")

    # Save instruction to disk
    data = new_instruction_file.partition('base64,')[2]
    image_data = b64decode(data)
    path = 'instructions/%d.png' % instruction.id
    with open(path, 'wb') as f:
        f.write(image_data)

    instruction.filepath = path
    instruction.save()

    return HttpResponse(status=200)


def reset(request):
    Instruction.objects.all().delete()





def main_app(request):
    context = {
        'images': Image.get_relevant_images()
    }
    return TemplateResponse(request, "main/main_app.html", context)


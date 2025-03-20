import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { MessageSquare, Phone } from "lucide-react"
import { useRouter } from "next/navigation"

interface ChoiceDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
}

export default function ChoiceDialog({ open, onOpenChange }: ChoiceDialogProps) {
  const router = useRouter()

  const handleChatClick = () => {
    router.push("/chat")
    onOpenChange(false)
  }

  const handleCallClick = () => {
    router.push("/call")
    onOpenChange(false)
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[550px] p-6">
        <DialogHeader>
          <DialogTitle className="text-center text-primary text-2xl mb-2">Wie möchten Sie mit unserem KI Berater kommunizieren?</DialogTitle>
        </DialogHeader>
        <div className="grid grid-cols-2 gap-6 mt-8">
          <button
            onClick={handleChatClick}
            className="flex flex-col items-center p-8 space-y-6 bg-white border border-primary/20 rounded-lg hover:bg-primary/5 transition-colors"
          >
            <MessageSquare className="h-16 w-16 text-primary" />
            <span className="text-center font-medium text-lg">Mit KI Berater Chatten</span>
          </button>
          <button
            onClick={handleCallClick}
            className="flex flex-col items-center p-8 space-y-6 bg-white border border-primary/20 rounded-lg hover:bg-primary/5 transition-colors"
          >
            <Phone className="h-16 w-16 text-primary" />
            <span className="text-center font-medium text-lg">Mit KI Berater telefonieren</span>
          </button>
        </div>
      </DialogContent>
    </Dialog>
  )
} 